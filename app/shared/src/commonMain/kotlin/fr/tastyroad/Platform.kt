package fr.tastyroad

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform