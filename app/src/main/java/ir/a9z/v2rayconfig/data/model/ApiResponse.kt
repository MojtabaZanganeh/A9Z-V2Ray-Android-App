package ir.a9z.v2rayconfig.data.model

data class SubResponse(
    val sub: String
)

data class ConfigResponse(
    val config: List<String>
)

data class LastUpdateResponse(
    val count: Int,
    val timestamp: String
) 