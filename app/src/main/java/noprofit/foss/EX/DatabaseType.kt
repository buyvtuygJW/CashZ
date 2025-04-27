package noprofit.foss.EX

//can adapt as per app basis.
enum class DatabaseType {
    CASHEW,
    ACTUAL,
    NOSQL,
    NOSQL_UNSAFERAW,
    UNKNOWN
}

fun identifyDatabaseType(fieldNames: List<String>): DatabaseType {
// Step 3: Identify Database Type
    return when {
        fieldNames.contains("date") -> DatabaseType.ACTUAL
        fieldNames.contains("date_created") -> DatabaseType.CASHEW
        else -> DatabaseType.UNKNOWN
    }
}

//DatabaseType.ACTUAL to nosql
private val fieldMappingACTUALdb =mapOf(
        "acct" to "name",
        "amount" to "amount",
        "category" to "note",
        "date" to "date_created",
        "tombstone" to "paid"
    )

//DatabaseType.Cashew
private val fieldMappingCashewdb =mapOf(
        "name" to "name",
        "amount" to "amount",
        "note" to "note",
        "date_created" to "date_created",
        "date_time_modified" to "date_time_modified",
        "original_date_due" to "original_date_due",
        "income" to "income",
        "paid" to "paid",
        "skip_paid" to "skip_paid"
    )

fun getdbtonosqlmapping(dbtype:DatabaseType): Map<String, String>{
    val fieldMapping = when (dbtype) {
        DatabaseType.ACTUAL-> fieldMappingACTUALdb
        DatabaseType.CASHEW -> fieldMappingCashewdb
        else -> mapOf<String, String>() // Default to an empty map if the database type is unknown
    }
    return fieldMapping
}

fun listofsupporteddb():List<String>{
return listOf("Actual", "Cashew")// Creating an immutable list of strings
    //mutableListOf("Apple", "Banana", "Cherry")// Creating a mutable list of strings
}

fun mapStringToEnum(input: String): DatabaseType?{
    return try {
        DatabaseType.valueOf(input.uppercase())
    } catch (e: IllegalArgumentException) {
        null // Return null if the input does not match any enum constant
    }
}
