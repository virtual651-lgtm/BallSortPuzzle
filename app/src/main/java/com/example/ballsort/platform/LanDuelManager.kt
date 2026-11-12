/*
 * com.example.ballsort.platform
 * Aynı Wi-Fi ağındaki cihazlar arasında NsdManager + TCP Socket tabanlı canlı düello yöneticisi.
 */
package com.example.ballsort.platform

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject
import javax.inject.Singleton

data class DiscoveredRoom(
    val name: String,
    val host: String,
    val port: Int,
)

sealed interface LanDuelState {
    data object Idle : LanDuelState
    data class Hosting(val localIp: String, val port: Int, val seed: Long) : LanDuelState
    data class Browsing(val rooms: List<DiscoveredRoom>) : LanDuelState
    data object Connecting : LanDuelState
    data class InMatch(
        val seed: Long,
        val opponentCompleted: Int = 0,
        val opponentTotal: Int = 7,
        val opponentMoves: Int = 0,
        val opponentWon: Boolean = false,
        val opponentDisconnected: Boolean = false,
    ) : LanDuelState
    data class Error(val message: String) : LanDuelState
}

interface LanDuelTransport {
    val state: StateFlow<LanDuelState>
    fun startHosting()
    fun startBrowsing()
    fun connectTo(host: String, port: Int)
    fun sendProgress(completedTubes: Int, totalColors: Int, moves: Int, won: Boolean)
    fun close()
}

object NoOpLanDuelTransport : LanDuelTransport {
    private val _state = MutableStateFlow<LanDuelState>(LanDuelState.Idle)
    override val state: StateFlow<LanDuelState> = _state.asStateFlow()
    override fun startHosting() = Unit
    override fun startBrowsing() = Unit
    override fun connectTo(host: String, port: Int) = Unit
    override fun sendProgress(completedTubes: Int, totalColors: Int, moves: Int, won: Boolean) = Unit
    override fun close() = Unit
}

@Singleton
class LanDuelManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : LanDuelTransport {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val nsdManager: NsdManager? = context.getSystemService(Context.NSD_SERVICE) as? NsdManager

    private val _state = MutableStateFlow<LanDuelState>(LanDuelState.Idle)
    override val state: StateFlow<LanDuelState> = _state.asStateFlow()

    private var serverSocket: ServerSocket? = null
    private var clientSocket: Socket? = null
    private var writer: PrintWriter? = null
    private var ioJob: Job? = null
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    override fun startHosting() {
        close()
        val seed = System.currentTimeMillis()
        ioJob = scope.launch {
            try {
                val server = runCatching { ServerSocket(DEFAULT_PORT) }.getOrElse { ServerSocket(0) }
                serverSocket = server
                val port = server.localPort
                val localIp = findLocalIpv4() ?: "127.0.0.1"
                _state.value = LanDuelState.Hosting(localIp = localIp, port = port, seed = seed)
                registerNsdService(port)

                val accepted = server.accept()
                unregisterNsd()
                clientSocket = accepted
                val out = PrintWriter(accepted.getOutputStream(), true)
                writer = out
                out.println("START:$seed")
                _state.value = LanDuelState.InMatch(seed = seed)
                readLoop(accepted, seed)
            } catch (e: Exception) {
                if (_state.value !is LanDuelState.Idle) {
                    _state.value = LanDuelState.Error(e.message ?: "Host error")
                }
            }
        }
    }

    override fun startBrowsing() {
        close()
        _state.value = LanDuelState.Browsing(emptyList())
        val mgr = nsdManager ?: return
        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) = Unit
            override fun onDiscoveryStopped(serviceType: String) = Unit
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                runCatching { mgr.stopServiceDiscovery(this) }
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                runCatching { mgr.stopServiceDiscovery(this) }
            }
            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                val current = (_state.value as? LanDuelState.Browsing)?.rooms ?: return
                _state.value = LanDuelState.Browsing(current.filterNot { it.name == serviceInfo.serviceName })
            }
            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (!serviceInfo.serviceType.contains("renktupleri")) return
                resolveNsdService(mgr, serviceInfo)
            }
        }
        discoveryListener = listener
        runCatching {
            mgr.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, listener)
        }
    }

    @Suppress("DEPRECATION")
    private fun resolveNsdService(mgr: NsdManager, info: NsdServiceInfo) {
        runCatching {
            mgr.resolveService(
                info,
                object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) = Unit
                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        val hostAddr = serviceInfo.host?.hostAddress ?: return
                        val room = DiscoveredRoom(
                            name = serviceInfo.serviceName,
                            host = hostAddr,
                            port = serviceInfo.port,
                        )
                        val cur = (_state.value as? LanDuelState.Browsing)?.rooms ?: return
                        if (cur.none { it.host == room.host && it.port == room.port }) {
                            _state.value = LanDuelState.Browsing(cur + room)
                        }
                    }
                },
            )
        }
    }

    override fun connectTo(host: String, port: Int) {
        close()
        _state.value = LanDuelState.Connecting
        ioJob = scope.launch {
            try {
                val sock = Socket()
                sock.connect(InetSocketAddress(host.trim(), port), 5_000)
                clientSocket = sock
                writer = PrintWriter(sock.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(sock.getInputStream()))
                val firstLine = reader.readLine() ?: throw IllegalStateException("Connection closed")
                val seed = firstLine.removePrefix("START:").toLongOrNull()
                    ?: throw IllegalStateException("Invalid handshake")
                _state.value = LanDuelState.InMatch(seed = seed)
                readLines(reader, seed)
            } catch (e: Exception) {
                _state.value = LanDuelState.Error(e.message ?: "Connect error")
            }
        }
    }

    override fun sendProgress(completedTubes: Int, totalColors: Int, moves: Int, won: Boolean) {
        val out = writer ?: return
        scope.launch {
            runCatching {
                out.println("PROG:$completedTubes:$totalColors:$moves:${if (won) 1 else 0}")
            }
        }
    }

    private fun readLoop(socket: Socket, seed: Long) {
        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        readLines(reader, seed)
    }

    private fun readLines(reader: BufferedReader, seed: Long) {
        try {
            while (true) {
                val line = reader.readLine() ?: break
                if (line.startsWith("PROG:")) {
                    val parts = line.removePrefix("PROG:").split(":")
                    if (parts.size >= 4) {
                        val comp = parts[0].toIntOrNull() ?: 0
                        val total = parts[1].toIntOrNull() ?: 7
                        val moves = parts[2].toIntOrNull() ?: 0
                        val won = parts[3] == "1"
                        val cur = _state.value as? LanDuelState.InMatch ?: LanDuelState.InMatch(seed)
                        _state.value = cur.copy(
                            opponentCompleted = comp,
                            opponentTotal = total,
                            opponentMoves = moves,
                            opponentWon = cur.opponentWon || won,
                        )
                    }
                }
            }
        } catch (_: Exception) {
        } finally {
            val cur = _state.value
            if (cur is LanDuelState.InMatch && !cur.opponentWon) {
                _state.value = cur.copy(opponentDisconnected = true)
            }
        }
    }

    private fun registerNsdService(port: Int) {
        val mgr = nsdManager ?: return
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "RenkTupleri-${Build.MODEL.take(10)}"
            serviceType = SERVICE_TYPE
            setPort(port)
        }
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) = Unit
            override fun onRegistrationFailed(info: NsdServiceInfo, errorCode: Int) = Unit
            override fun onServiceUnregistered(info: NsdServiceInfo) = Unit
            override fun onUnregistrationFailed(info: NsdServiceInfo, errorCode: Int) = Unit
        }
        registrationListener = listener
        runCatching { mgr.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, listener) }
    }

    private fun unregisterNsd() {
        val mgr = nsdManager ?: return
        registrationListener?.let { runCatching { mgr.unregisterService(it) } }
        registrationListener = null
    }

    override fun close() {
        _state.value = LanDuelState.Idle
        ioJob?.cancel()
        ioJob = null
        unregisterNsd()
        discoveryListener?.let { l -> runCatching { nsdManager?.stopServiceDiscovery(l) } }
        discoveryListener = null
        runCatching { writer?.close() }
        writer = null
        runCatching { clientSocket?.close() }
        clientSocket = null
        runCatching { serverSocket?.close() }
        serverSocket = null
    }

    private fun findLocalIpv4(): String? = runCatching {
        NetworkInterface.getNetworkInterfaces()?.toList()
            ?.flatMap { it.inetAddresses.toList() }
            ?.firstOrNull { !it.isLoopbackAddress && it is Inet4Address }
            ?.hostAddress
    }.getOrNull()

    companion object {
        private const val SERVICE_TYPE = "_renktupleri._tcp."
        const val DEFAULT_PORT = 47777
    }
}

