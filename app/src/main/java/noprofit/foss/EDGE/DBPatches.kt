package noprofit.foss.EDGE

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

//as the file name mention it is for compatibility patch between db,most have some assumptions and are not lossless.

/**
 * Function to get the category primary key (categorypk) from the first table.
 * @param db The SQLiteDatabase instance.
 * @param tableName The name of the table to query.
 * @param columnName The column to retrieve.
 * @param condition The condition for querying (e.g., "id = 1").
 * @return The category primary key (categorypk) or null if not found.
 */
fun getCategoryPKFromTable(
    db: SQLiteDatabase,
    tableName: String,
    columnName: String,
    condition: String
): Int? {
    var categoryPK: Int? = null
    val query = "SELECT $columnName FROM $tableName WHERE $condition"
    val cursor: Cursor = db.rawQuery(query, null)
    if (cursor.moveToFirst()) {
        categoryPK = cursor.getInt(cursor.getColumnIndexOrThrow(columnName))
    }
    cursor.close()
    return categoryPK
}


/**
 * Function to get the category primary key (categorypk) from the first table.
 * @param db The SQLiteDatabase instance.
 * @param tableName The name of the table to query.
 * @param columnName The column to retrieve.
 * @param condition The condition for querying (e.g., "id = 1"). or eg code>    val finalcond = if (condition is String) "id = '$condition'" else condition
 * @return The category primary key (categorypk) or null if not found.
 */
fun getStrFieldFromTable(
    db: SQLiteDatabase,
    tableName: String,
    columnName: String,
    condition: String
): String? {
    var categoryPK: String? = null
    val query = "SELECT $columnName FROM $tableName WHERE $condition"
    val cursor: Cursor = db.rawQuery(query, null)
    if (cursor.moveToFirst()) {
        categoryPK = cursor.getString(cursor.getColumnIndexOrThrow(columnName))
    }
    cursor.close()
    return categoryPK
}

//the eg is categoryPK, as the int key for 2nd table called category.Extremely useful,if we need for 2nd table
fun getintFieldFromTablenocond(
    db: SQLiteDatabase,
    tableName: String,
    columnName: String,
): Int? {
    var categoryPK: Int? = null
    val query = "SELECT $columnName FROM $tableName"
    val cursor: Cursor = db.rawQuery(query, null)
    if (cursor.moveToFirst()) {
        categoryPK = cursor.getInt(cursor.getColumnIndexOrThrow(columnName))
    }
    cursor.close()
    return categoryPK
}

//the eg is categoryPK, as the int key for 2nd table called category.Extremely useful,if we need for 2nd table
fun getStrFieldFromTablenocond(
    db: SQLiteDatabase,
    tableName: String,
    columnName: String,
): String? {
    var categoryPK: String? = null
    val query = "SELECT $columnName FROM $tableName"
    val cursor: Cursor = db.rawQuery(query, null)
    if (cursor.moveToFirst()) {
        categoryPK = cursor.getString(cursor.getColumnIndexOrThrow(columnName))
    }
    cursor.close()
    return categoryPK
}

/**
 * Function to get the category name from the category table.
 * @param db The SQLiteDatabase instance.
 * @param categoryTableName The name of the category table.
 * @param categoryPK The primary key of the category to query.
 * @return The category name or null if not found.
 */
fun getCategoryName(
    db: SQLiteDatabase,
    categoryTableName: String,
    categorytblkeyname: String,
    categoryPK: Any
): String? {
    var categoryName: String? = null
    val sqlreadycategorypkval = if (categoryPK is String) "'$categoryPK'" else categoryPK //to prevent>unrecognized token: "506e8d9d" in ...
    val query = "SELECT name FROM $categoryTableName WHERE $categorytblkeyname = $sqlreadycategorypkval"
    val cursor: Cursor = db.rawQuery(query, null)
    if (cursor.moveToFirst()) {
        categoryName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
    }
    cursor.close()
    return categoryName
}

/**
 * Function to get the category name by chaining the queries.
 * @param db The SQLiteDatabase instance.
 * @param tableName The name of the first table to query.
 * @param columnName The column in the first table containing the categorypk.
 * @param condition The condition for querying the first table.
 * @param categoryTableName The name of the category table.
 * @return The category name or null if not found.
 */
fun getCategoryNameFromBothTables(
    db: SQLiteDatabase,
    tableName: String,
    columnName: String,
    condition: String,
    categoryTableName: String,
    categorytblkeyname: String
): String? {
    // Step 1: Get the category primary key from the first table
    val categoryPK = getCategoryPKFromTable(db, tableName, columnName, condition)

    // Step 2: Get the category name from the category table
    return if (categoryPK != null) {
        getCategoryName(db, categoryTableName,categorytblkeyname, categoryPK)
    } else {
        null
    }
}

/**
 *
 * Function to get the Cashew db category name by chaining the queries.TESTED AT 2025/4 works.
 * @param db The SQLiteDatabase instance.
 * @param tableName The name of the first table to query.
 * @param columnName The column in the first table containing the categorypk.
 * @param categoryTableName The name of the category table.
 * @param categorytblkeyname The key/column of the category table that uses the data from 1st table to match.
 * @return The category name or null if not found.
 */
fun CashewgetCategoryNameFromBothTables(
    db: SQLiteDatabase,
    tbl1Name: String,
    tbl1columnName: String,
    chained2ndTableName: String,
    categorytblkeyname: String
): String? {
    // Step 1: Get the category primary key from the first table
    val categoryPK = getintFieldFromTablenocond(db, tbl1Name, tbl1columnName)
    //android.util.Log.d("Category PK,dbpatch>","$categoryPK")//debug log
    // Step 2: Get the category name from the category table
    return if (categoryPK != null) {
        getCategoryName(db, chained2ndTableName, categorytblkeyname,categoryPK)
    } else {
        null
    }
}

/**
 *
 * Function to get the Actual db category name by chaining the queries.TESTED AT 2025/4 works.
 * The main difference is actualdb use uuid not int id so need a seperate function.
 * @param db The SQLiteDatabase instance.
 * @param tableName The name of the first table to query.
 * @param columnName The column in the first table containing the categorypk.
 * @param categoryTableName The name of the category table.
 * @param categorytblkeyname The key/column of the category table that uses the data from 1st table to match.
 * @return The category name or null if not found.
 */
fun ActualdbgetCategoryNameFromTables(
    db: SQLiteDatabase,
    tbl1Name: String,
    tbl1columnName: String,
    chained2ndTableName: String,
    categorytblkeyname: String,
    tbl1id: String
): String? {
    // Step 1: Get the category primary key from the first table
    val finalcond = if (tbl1id is String) "id = '$tbl1id'" else tbl1id
    var categoryPK = getStrFieldFromTable(db, tbl1Name, tbl1columnName,finalcond)
    //android.util.Log.d("Category PK,dbpatch>","$categoryPK")//debug use
    // Step 2: Get the category name from the category table
    return if (categoryPK != null) {
        getCategoryName(db, chained2ndTableName, categorytblkeyname,categoryPK)
    } else {
        null
    }
}


fun dropexistingcashewtables(db: SQLiteDatabase){
    db.beginTransaction()
    val dropStatements = listOf(
        "DROP TABLE IF EXISTS app_settings",
        "DROP TABLE IF EXISTS associated_titles",
        "DROP TABLE IF EXISTS budgets",
        "DROP TABLE IF EXISTS category_budget_limits",
        "DROP TABLE IF EXISTS delete_logs",
        "DROP TABLE IF EXISTS wallets",
        "DROP TABLE IF EXISTS objectives",
        "DROP TABLE IF EXISTS scanner_templates"
    )
    for (sql in dropStatements) {
        db.execSQL(sql)
    }
    db.setTransactionSuccessful()
    db.endTransaction()
}

fun createcategorytblandinsertdtforcashew(db: SQLiteDatabase){
    //do not need NULL keyword to show allow null>Some database engines (like SQLite) don’t explicitly require the NULL keyword because it’s implied by default.
    var createTableQuery = "DROP TABLE IF EXISTS categories;"
    db.execSQL(createTableQuery)
    //1st nuke existing table to prevent>table "app_settings" already exists in "
    createTableQuery = "CREATE TABLE \"categories\" (\"category_pk\" TEXT NOT NULL, \"name\" TEXT NOT NULL, \"colour\" TEXT, \"icon_name\" TEXT, \"emoji_icon_name\" TEXT, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER  DEFAULT 1743296390, \"order\" INTEGER NOT NULL, \"income\" INTEGER NOT NULL DEFAULT 0 CHECK (\"income\" IN (0, 1)), \"method_added\" INTEGER , \"main_category_pk\" TEXT DEFAULT NULL REFERENCES categories (category_pk), PRIMARY KEY (\"category_pk\"));"
    db.execSQL(createTableQuery)
    //the order is a reserved keyword need escape with "order"
    val insertsStatements = listOf(
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('1', 'Dining', '0xff607d8b', 'cutlery.png', NULL, '1743296390', '-62167219200', '0', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('3', 'Shopping', '0xffe91e63', 'shopping.png', NULL, '1743296390', '-62167219200', '2', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('4', 'Transit', '0xffffeb3b', 'tram.png', NULL, '1743296390', '-62167219200', '3', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('5', 'Entertainment', '0xff2196f3', 'popcorn.png', NULL, '1743296390', '-62167219200', '4', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('6', 'Bills & Fees', '0xff4caf50', 'bills.png', NULL, '1743296390', '-62167219200', '5', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('7', 'Gifts', '0xfff44336', 'gift.png', NULL, '1743296390', '-62167219200', '6', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('8', 'Beauty', '0xff9c27b0', 'flower.png', NULL, '1743296390', '-62167219200', '8', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('9', 'Work', '0xff795548', 'briefcase.png', NULL, '1743296390', '-62167219200', '9', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('10', 'Travel', '0xffff9800', 'plane.png', NULL, '1743296390', '-62167219200', '10', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('11', 'Income', '0xff9575cd', 'coin.png', NULL, '1743296390', '1743296425', '11', '1', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('2', 'Groceries', '0xff4caf50', 'groceries.png', NULL, '1743296390', '1743296455', '1', '0', NULL, NULL)"
    )
    for (sql in insertsStatements) { db.execSQL(sql)}
    //after some test confirmed MUST have at least created the table 1st.
    createTableQuery = "CREATE TABLE \"app_settings\" (\"settings_pk\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"settings_j_s_o_n\" TEXT NOT NULL, \"date_updated\" INTEGER NOT NULL)"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"associated_titles\" (\"associated_title_pk\" TEXT NOT NULL, \"category_fk\" TEXT NOT NULL REFERENCES categories (category_pk), \"title\" TEXT NOT NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"order\" INTEGER NOT NULL, \"is_exact_match\" INTEGER NOT NULL DEFAULT 0 CHECK (\"is_exact_match\" IN (0, 1)), PRIMARY KEY (\"associated_title_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"budgets\" (\"budget_pk\" TEXT NOT NULL, \"name\" TEXT NOT NULL, \"amount\" REAL NOT NULL, \"colour\" TEXT NULL, \"start_date\" INTEGER NOT NULL, \"end_date\" INTEGER NOT NULL, \"wallet_fks\" TEXT NULL, \"category_fks\" TEXT NULL, \"category_fks_exclude\" TEXT NULL, \"income\" INTEGER NOT NULL DEFAULT 0 CHECK (\"income\" IN (0, 1)), \"archived\" INTEGER NOT NULL DEFAULT 0 CHECK (\"archived\" IN (0, 1)), \"added_transactions_only\" INTEGER NOT NULL DEFAULT 0 CHECK (\"added_transactions_only\" IN (0, 1)), \"period_length\" INTEGER NOT NULL, \"reoccurrence\" INTEGER NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"pinned\" INTEGER NOT NULL DEFAULT 0 CHECK (\"pinned\" IN (0, 1)), \"order\" INTEGER NOT NULL, \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), \"budget_transaction_filters\" TEXT NULL DEFAULT NULL, \"member_transaction_filters\" TEXT NULL DEFAULT NULL, \"shared_key\" TEXT NULL, \"shared_owner_member\" INTEGER NULL, \"shared_date_updated\" INTEGER NULL, \"shared_members\" TEXT NULL, \"shared_all_members_ever\" TEXT NULL, \"is_absolute_spending_limit\" INTEGER NOT NULL DEFAULT 0 CHECK (\"is_absolute_spending_limit\" IN (0, 1)), PRIMARY KEY (\"budget_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"category_budget_limits\" (\"category_limit_pk\" TEXT NOT NULL, \"category_fk\" TEXT NOT NULL REFERENCES categories (category_pk), \"budget_fk\" TEXT NOT NULL REFERENCES budgets (budget_pk), \"amount\" REAL NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), PRIMARY KEY (\"category_limit_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"delete_logs\" (\"delete_log_pk\" TEXT NOT NULL, \"entry_pk\" TEXT NOT NULL, \"type\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NOT NULL DEFAULT 1743296390, PRIMARY KEY (\"delete_log_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"objectives\" (\"objective_pk\" TEXT NOT NULL, \"type\" INTEGER NOT NULL DEFAULT 0, \"name\" TEXT NOT NULL, \"amount\" REAL NOT NULL, \"order\" INTEGER NOT NULL, \"colour\" TEXT NULL, \"date_created\" INTEGER NOT NULL, \"end_date\" INTEGER NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"icon_name\" TEXT NULL, \"emoji_icon_name\" TEXT NULL, \"income\" INTEGER NOT NULL DEFAULT 0 CHECK (\"income\" IN (0, 1)), \"pinned\" INTEGER NOT NULL DEFAULT 1 CHECK (\"pinned\" IN (0, 1)), \"archived\" INTEGER NOT NULL DEFAULT 0 CHECK (\"archived\" IN (0, 1)), \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), PRIMARY KEY (\"objective_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"scanner_templates\" (\"scanner_template_pk\" TEXT NOT NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"template_name\" TEXT NOT NULL, \"contains\" TEXT NOT NULL, \"title_transaction_before\" TEXT NOT NULL, \"title_transaction_after\" TEXT NOT NULL, \"amount_transaction_before\" TEXT NOT NULL, \"amount_transaction_after\" TEXT NOT NULL, \"default_category_fk\" TEXT NOT NULL REFERENCES categories (category_pk), \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), \"ignore\" INTEGER NOT NULL DEFAULT 0 CHECK (\"ignore\" IN (0, 1)), PRIMARY KEY (\"scanner_template_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"wallets\" (\"wallet_pk\" TEXT NOT NULL, \"name\" TEXT NOT NULL, \"colour\" TEXT NULL, \"icon_name\" TEXT NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"order\" INTEGER NOT NULL, \"currency\" TEXT NULL, \"currency_format\" TEXT NULL, \"decimals\" INTEGER NOT NULL DEFAULT 2, \"home_page_widget_display\" TEXT NULL DEFAULT NULL, PRIMARY KEY (\"wallet_pk\"))"
    db.execSQL(createTableQuery)
}

//this added a "unknown" category to prevent error.
fun createcategorytblandinsertdtforcashewv2(db: SQLiteDatabase){
    //do not need NULL keyword to show allow null>Some database engines (like SQLite) don’t explicitly require the NULL keyword because it’s implied by default.
    var createTableQuery = "DROP TABLE IF EXISTS categories;"
    db.execSQL(createTableQuery)
    //1st nuke existing table to prevent>table "app_settings" already exists in "
    createTableQuery = "CREATE TABLE \"categories\" (\"category_pk\" TEXT NOT NULL, \"name\" TEXT NOT NULL, \"colour\" TEXT, \"icon_name\" TEXT, \"emoji_icon_name\" TEXT, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER  DEFAULT 1743296390, \"order\" INTEGER NOT NULL, \"income\" INTEGER NOT NULL DEFAULT 0 CHECK (\"income\" IN (0, 1)), \"method_added\" INTEGER , \"main_category_pk\" TEXT DEFAULT NULL REFERENCES categories (category_pk), PRIMARY KEY (\"category_pk\"));"
    db.execSQL(createTableQuery)
    //the order is a reserved keyword need escape with "order"
    val insertsStatements = listOf(
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('1', 'Dining', '0xff607d8b', 'cutlery.png', NULL, '1743296390', '-62167219200', '0', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('3', 'Shopping', '0xffe91e63', 'shopping.png', NULL, '1743296390', '-62167219200', '2', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('4', 'Transit', '0xffffeb3b', 'tram.png', NULL, '1743296390', '-62167219200', '3', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('5', 'Entertainment', '0xff2196f3', 'popcorn.png', NULL, '1743296390', '-62167219200', '4', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('6', 'Bills & Fees', '0xff4caf50', 'bills.png', NULL, '1743296390', '-62167219200', '5', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('7', 'Gifts', '0xfff44336', 'gift.png', NULL, '1743296390', '-62167219200', '6', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('8', 'Beauty', '0xff9c27b0', 'flower.png', NULL, '1743296390', '-62167219200', '8', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('9', 'Work', '0xff795548', 'briefcase.png', NULL, '1743296390', '-62167219200', '9', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('10', 'Travel', '0xffff9800', 'plane.png', NULL, '1743296390', '-62167219200', '10', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('11', 'Income', '0xff9575cd', 'coin.png', NULL, '1743296390', '1743296425', '11', '1', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('2', 'Groceries', '0xff4caf50', 'groceries.png', NULL, '1743296390', '1743296455', '1', '0', NULL, NULL)",
        "INSERT INTO categories (category_pk, name, colour, icon_name, emoji_icon_name, date_created, date_time_modified, \"order\", income, method_added, main_category_pk) VALUES ('12', 'Unknown', '0xff4caf50', '', NULL, '1743296390', '1743296455', '12', '0', NULL, NULL)"
    )
    for (sql in insertsStatements) { db.execSQL(sql)}
    //after some test confirmed MUST have at least created the table 1st.
    createTableQuery = "CREATE TABLE \"app_settings\" (\"settings_pk\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, \"settings_j_s_o_n\" TEXT NOT NULL, \"date_updated\" INTEGER NOT NULL)"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"associated_titles\" (\"associated_title_pk\" TEXT NOT NULL, \"category_fk\" TEXT NOT NULL REFERENCES categories (category_pk), \"title\" TEXT NOT NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"order\" INTEGER NOT NULL, \"is_exact_match\" INTEGER NOT NULL DEFAULT 0 CHECK (\"is_exact_match\" IN (0, 1)), PRIMARY KEY (\"associated_title_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"budgets\" (\"budget_pk\" TEXT NOT NULL, \"name\" TEXT NOT NULL, \"amount\" REAL NOT NULL, \"colour\" TEXT NULL, \"start_date\" INTEGER NOT NULL, \"end_date\" INTEGER NOT NULL, \"wallet_fks\" TEXT NULL, \"category_fks\" TEXT NULL, \"category_fks_exclude\" TEXT NULL, \"income\" INTEGER NOT NULL DEFAULT 0 CHECK (\"income\" IN (0, 1)), \"archived\" INTEGER NOT NULL DEFAULT 0 CHECK (\"archived\" IN (0, 1)), \"added_transactions_only\" INTEGER NOT NULL DEFAULT 0 CHECK (\"added_transactions_only\" IN (0, 1)), \"period_length\" INTEGER NOT NULL, \"reoccurrence\" INTEGER NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"pinned\" INTEGER NOT NULL DEFAULT 0 CHECK (\"pinned\" IN (0, 1)), \"order\" INTEGER NOT NULL, \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), \"budget_transaction_filters\" TEXT NULL DEFAULT NULL, \"member_transaction_filters\" TEXT NULL DEFAULT NULL, \"shared_key\" TEXT NULL, \"shared_owner_member\" INTEGER NULL, \"shared_date_updated\" INTEGER NULL, \"shared_members\" TEXT NULL, \"shared_all_members_ever\" TEXT NULL, \"is_absolute_spending_limit\" INTEGER NOT NULL DEFAULT 0 CHECK (\"is_absolute_spending_limit\" IN (0, 1)), PRIMARY KEY (\"budget_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"category_budget_limits\" (\"category_limit_pk\" TEXT NOT NULL, \"category_fk\" TEXT NOT NULL REFERENCES categories (category_pk), \"budget_fk\" TEXT NOT NULL REFERENCES budgets (budget_pk), \"amount\" REAL NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), PRIMARY KEY (\"category_limit_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"delete_logs\" (\"delete_log_pk\" TEXT NOT NULL, \"entry_pk\" TEXT NOT NULL, \"type\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NOT NULL DEFAULT 1743296390, PRIMARY KEY (\"delete_log_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"objectives\" (\"objective_pk\" TEXT NOT NULL, \"type\" INTEGER NOT NULL DEFAULT 0, \"name\" TEXT NOT NULL, \"amount\" REAL NOT NULL, \"order\" INTEGER NOT NULL, \"colour\" TEXT NULL, \"date_created\" INTEGER NOT NULL, \"end_date\" INTEGER NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"icon_name\" TEXT NULL, \"emoji_icon_name\" TEXT NULL, \"income\" INTEGER NOT NULL DEFAULT 0 CHECK (\"income\" IN (0, 1)), \"pinned\" INTEGER NOT NULL DEFAULT 1 CHECK (\"pinned\" IN (0, 1)), \"archived\" INTEGER NOT NULL DEFAULT 0 CHECK (\"archived\" IN (0, 1)), \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), PRIMARY KEY (\"objective_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"scanner_templates\" (\"scanner_template_pk\" TEXT NOT NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"template_name\" TEXT NOT NULL, \"contains\" TEXT NOT NULL, \"title_transaction_before\" TEXT NOT NULL, \"title_transaction_after\" TEXT NOT NULL, \"amount_transaction_before\" TEXT NOT NULL, \"amount_transaction_after\" TEXT NOT NULL, \"default_category_fk\" TEXT NOT NULL REFERENCES categories (category_pk), \"wallet_fk\" TEXT NOT NULL DEFAULT '0' REFERENCES wallets (wallet_pk), \"ignore\" INTEGER NOT NULL DEFAULT 0 CHECK (\"ignore\" IN (0, 1)), PRIMARY KEY (\"scanner_template_pk\"))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE \"wallets\" (\"wallet_pk\" TEXT NOT NULL, \"name\" TEXT NOT NULL, \"colour\" TEXT NULL, \"icon_name\" TEXT NULL, \"date_created\" INTEGER NOT NULL, \"date_time_modified\" INTEGER NULL DEFAULT 1743296390, \"order\" INTEGER NOT NULL, \"currency\" TEXT NULL, \"currency_format\" TEXT NULL, \"decimals\" INTEGER NOT NULL DEFAULT 2, \"home_page_widget_display\" TEXT NULL DEFAULT NULL, PRIMARY KEY (\"wallet_pk\"))"
    db.execSQL(createTableQuery)
}

//to convert back to sql.Assume the db transaction open &close is handled by caller.
fun nosqltocashewsqlpatch(contentValues: ContentValues): ContentValues{
    contentValues.put("paid",1)//to prevent nothing?
    contentValues.put("skip_paid",1)//to prevent nothing?
    var a=contentValues.get("date_created")as? Long ?:0// Cast to Long, or use another type like String if applicable
    contentValues.put("date_time_modified",a+1)//assume is sqllite so it support 64 bit modify date.
    contentValues.put("original_date_due",a-10)//should auto use default value?

    val nonnullablestrlist: List<String> = listOf("note","category_fk")
    utils.ensureNonNullValues(contentValues, nonnullablestrlist, "")
    //val nonnullableintlist: List<String> = listOf("income")//v1 naive set as 1
    //utils.ensureNonNullValues(contentValues, nonnullableintlist,1)

    var amt=contentValues.get("amount")as? Double
    if((amt?: 0.0)>0){contentValues.put("income",1)}else{contentValues.put("income",0)}// Default to 0.0 if amt is null

    //unsure useful or not but try fix
    contentValues.put("period_length",1)
    contentValues.put("reoccurrence",3)

    return contentValues
}

//assume the note is also valid in cashew.
fun getcategorymatchedpkcashew(contentValues: ContentValues,db: SQLiteDatabase):String?{
    var noteValue=contentValues.get("note")as? String ?: return null
    //android.util.Log.d("Get category name,patch>","$noteValue")//debug use
    // Step 2: Prepare the SQL query
    val query = "SELECT category_pk FROM categories WHERE name = ?"
    val cursor = db.rawQuery(query, arrayOf(noteValue))

    // Step 3: Check the result and retrieve the id
    return if (cursor.moveToFirst()) {
        val categoryId = cursor.getString(0) // Get the 'id' column value
        cursor.close()
        categoryId // Return the id
    } else {
        cursor.close()
        null // No match found, return null
    }
}

/**
 * As some category in Actual db is not in Cashew we need to remap. YUP IN FACT IT IS THE MAIN CHECK,IF HAD OLD DB STRUCTURE NOT UPDATED SOMETIMES NEED CLEAR app storage AND REIMPORT FOR THIS V2.
//assume the note is also valid in cashew.
*/
fun getcategorymatchedpkcashewv2(contentValues: ContentValues,db: SQLiteDatabase):String?{
    var noteValue=contentValues.get("note")as? String ?: return null
    //android.util.Log.d("Get category name,patch>","$noteValue")//debug use
    // Step 2: Prepare the SQL query
    val query = "SELECT category_pk FROM categories WHERE name = ?"
    val cursor = db.rawQuery(query, arrayOf(noteValue))


    // Step 3: Check the result and retrieve the id
    return if (cursor.moveToFirst()) {
        val categoryId = cursor.getString(0) // Get the 'id' column value
        cursor.close()
        categoryId // Return the id
    } else {
        cursor.close()
        if (noteValue!=""){
            return when (noteValue) {
                "Starting Balances" -> "11"//Income
                "Bills" -> "6"//Bills & Fees
                "Bills (Flexible)" -> "6"//Bills & Fees
                "Food" -> "2"//Groceries
                else -> "12"
            }
        }else{return "12"}
    }
}

//try patch export as actual db
fun createcategorytblandinsertdtforactual(db: SQLiteDatabase){
    //do not need NULL keyword to show allow null>Some database engines (like SQLite) don’t explicitly require the NULL keyword because it’s implied by default.
    var createTableQuery = "DROP TABLE IF EXISTS categories;"
    db.execSQL(createTableQuery)
    //1st nuke existing table to prevent>table "app_settings" already exists in "
    createTableQuery ="CREATE TABLE categories\n" +
            " (id TEXT PRIMARY KEY,\n" +
            "  name TEXT,\n" +
            "  is_income INTEGER DEFAULT 0,\n" +
            "  cat_group TEXT,\n" +
            "  sort_order REAL,\n" +
            "  tombstone INTEGER DEFAULT 0, hidden BOOLEAN NOT NULL DEFAULT 0, goal_def TEXT DEFAULT null)"
    db.execSQL(createTableQuery)
    //the order is a reserved keyword need escape with "order"
    val insertsStatements = listOf(
""
    )
    for (sql in insertsStatements) { db.execSQL(sql)}
    //after some test confirmed MUST have at least created the table 1st.
    createTableQuery = "CREATE TABLE banks\n" +
            " (id TEXT PRIMARY KEY,\n" +
            "  bank_id TEXT,\n" +
            "  name TEXT,\n" +
            "  tombstone INTEGER DEFAULT 0)"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE custom_reports\n" +
            "  (\n" +
            "    id TEXT PRIMARY KEY,\n" +
            "    name TEXT,\n" +
            "    start_date TEXT,\n" +
            "    end_date TEXT,\n" +
            "    date_static INTEGER DEFAULT 0,\n" +
            "    date_range TEXT,\n" +
            "    mode TEXT DEFAULT 'total',\n" +
            "    group_by TEXT DEFAULT 'Category',\n" +
            "    balance_type TEXT DEFAULT 'Expense',\n" +
            "    show_empty INTEGER DEFAULT 0,\n" +
            "    show_offbudget INTEGER DEFAULT 0,\n" +
            "    show_hidden INTEGER DEFAULT 0,\n" +
            "    show_uncategorized INTEGER DEFAULT 0,\n" +
            "    selected_categories TEXT,\n" +
            "    graph_type TEXT DEFAULT 'BarGraph',\n" +
            "    conditions TEXT,\n" +
            "    conditions_op TEXT DEFAULT 'and',\n" +
            "    metadata TEXT,\n" +
            "    interval TEXT DEFAULT 'Monthly',\n" +
            "    color_scheme TEXT,\n" +
            "    tombstone INTEGER DEFAULT 0\n" +
            "  , include_current INTEGER DEFAULT 0)"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE notes\n" +
            "  (id TEXT PRIMARY KEY,\n" +
            "   note TEXT)"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE pending_transactions\n" +
            "  (id TEXT PRIMARY KEY,\n" +
            "   acct INTEGER,\n" +
            "   amount INTEGER,\n" +
            "   description TEXT,\n" +
            "   date TEXT,\n" +
            "   FOREIGN KEY(acct) REFERENCES accounts(id))"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE preferences\n" +
            "       (id TEXT PRIMARY KEY,\n" +
            "        value TEXT)"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE reflect_budgets\n" +
            "  (id TEXT PRIMARY KEY,\n" +
            "   month INTEGER,\n" +
            "   category TEXT,\n" +
            "   amount INTEGER DEFAULT 0,\n" +
            "   carryover INTEGER DEFAULT 0, goal INTEGER DEFAULT null, long_goal INTEGER DEFAULT null)"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE rules\n" +
            "  (id TEXT PRIMARY KEY,\n" +
            "   stage TEXT,\n" +
            "   conditions TEXT,\n" +
            "   actions TEXT,\n" +
            "   tombstone INTEGER DEFAULT 0, conditions_op TEXT DEFAULT 'and')"
    db.execSQL(createTableQuery)
    createTableQuery = "CREATE TABLE schedules\n" +
            "  (id TEXT PRIMARY KEY,\n" +
            "   rule TEXT,\n" +
            "   active INTEGER DEFAULT 0,\n" +
            "   completed INTEGER DEFAULT 0,\n" +
            "   posts_transaction INTEGER DEFAULT 0,\n" +
            "   tombstone INTEGER DEFAULT 0, name TEXT DEFAULT NULL)"
    db.execSQL(createTableQuery)
}

/**
fun createothertblsforcashew(db: SQLiteDatabase){
    //do not need NULL keyword to show allow null>Some database engines (like SQLite) don’t explicitly require the NULL keyword because it’s implied by default.
    var createTableQuery ="""
    """
    db.execSQL(createTableQuery)
}
 */
