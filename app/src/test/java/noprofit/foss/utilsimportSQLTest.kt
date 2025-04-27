import org.junit.Test
import org.junit.Assert.*
import noprofit.foss.importsql.SQLDBHelper

import java.io.File
import java.io.FileInputStream
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
//for android context

//import org.robolectric.annotation.Config

@RunWith(MockitoJUnitRunner::class)
//@RunWith(RobolectricTestRunner::class)
//@Config(manifest=Config.NONE)
class Utilsimportsqltest{

    /**
     * //STEP1 function per function test
     * Assume the utils Function Test Passed.
     */

    /**
    @Test
    fun Testimportcashewsqldb_directly() {
        // Arrange
        val actualdbver=3//need check

        // Act
        val mockContext = mock(Context::class.java)
        //val mockAssetManager = mock(AssetManager::class.java)
        val file = File("src/test/rsc/cashew-2025-01-14-23-49.sql")
        val inputStream = FileInputStream(file)
        val databaseHelper = SQLDBHelper(mockContext)
        databaseHelper.copySQLiteFile(inputStream)//if success then can check version correct or not.
        android.util.Log.d("test",databaseHelper.getImportedDatabaseVersion().toString())

        // Assert
        assertEquals(actualdbver, databaseHelper.getImportedDatabaseVersion())
    }*/


    @Test
    fun TestrecognizeCashew_assqlusingheader_ignoresubfix() {
        // Arrange
        val expectedData = true

        // Act
        val file = File("src/test/rsc/cashew-2025-01-14-23-49.sql")
        val inputStream = FileInputStream(file)
        val (isSQLiteFile, newInputStream) = SQLDBHelper.checkIfSQLiteFile(inputStream)

        // Assert
        assertEquals(expectedData, isSQLiteFile)
    }


}