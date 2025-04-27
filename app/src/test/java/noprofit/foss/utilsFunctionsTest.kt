import org.junit.Test
import org.junit.Assert.*

import java.io.File
import java.io.FileInputStream

class TestUtils {

    @Test
    fun testReadFileContent() {
        // Arrange
        val file = File("src/test/rsc/Testreadcontent.csv")
        val inputStream = FileInputStream(file)
        val expectedContent = "a" // Replace with the actual expected content

        // Act
        val actualContent = utils.readFileContent(inputStream)

        // Assert
        assertEquals(expectedContent, actualContent)
    }

}