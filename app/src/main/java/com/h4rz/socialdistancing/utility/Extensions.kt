package com.h4rz.socialdistancing.utility

/**
 * Created by Harsh Rajgor on 27/04/20.
 */

fun getHexadecimalIdentifierString(input: String): String {
    var identifier = ""
    val byteArray = input.toByteArray(Charsets.UTF_8)
    for (byte in byteArray)
        identifier += String.format("%02X", byte)
    val identifierLength = identifier.length
    if (identifierLength > 32)
        identifier = identifier.substring(0, 32)
    if (identifierLength < 32) {
        val diff = 32 - identifierLength
        identifier += "0".repeat(diff)
    }
    identifier =
        "${identifier.substring(0, 8)}-${identifier.substring(8, 12)}-${identifier.substring(
            12,
            16
        )}-${identifier.substring(16, 20)}-${identifier.substring(20)}"
    return identifier
}