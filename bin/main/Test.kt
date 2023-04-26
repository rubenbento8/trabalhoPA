import java.util.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.*


class CourseTest {
    @Test
    fun returnValidJsonStringTest() {
        // Arrange
        val course = JsonObject(mapOf(
            "uc" to JsonString("PA"),
            "ects" to JsonDouble(6.0),
            "data-exame" to JsonNull(),
            "inscritos" to JsonArray(listOf(
                JsonObject(mapOf(
                    "numero" to JsonInt(101101),
                    "nome" to JsonString("Dave Farley"),
                    "internacional" to JsonBoolean(true)
                )),
                JsonObject(mapOf(
                    "numero" to JsonInt(101102),
                    "nome" to JsonString("Martin Fowler"),
                    "internacional" to JsonBoolean(true)
                )),
                JsonObject(mapOf(
                    "numero" to JsonInt(26503),
                    "nome" to JsonString("André Santos"),
                    "internacional" to JsonBoolean(false)
                ))
            ))
        ))

        // Act
        val jsonString = course.toJsonString()

        // Assert
        val expectedOutput = "{\"uc\": \"PA\",\"ects\": 6.0,\"data-exame\": null,\"inscritos\": [{\"numero\": 101101,\"nome\": \"Dave Farley\",\"internacional\": true},{\"numero\": 101102,\"nome\": \"Martin Fowler\",\"internacional\": true},{\"numero\": 26503,\"nome\": \"André Santos\",\"internacional\": false}]}"
        assertEquals(expectedOutput, jsonString)
    }
}

class GetNumerosVisitorTest {

    @Test
    fun testGetNumerosVisitor() {
        val course = JsonObject(mapOf(
            "uc" to JsonString("PA"),
            "ects" to JsonDouble(6.0),
            "data-exame" to JsonNull(),
            "inscritos" to JsonArray(listOf(
                JsonObject(mapOf(
                    "numero" to JsonInt(101101),
                    "nome" to JsonString("Dave Farley"),
                    "internacional" to JsonBoolean(true)
                )),
                JsonObject(mapOf(
                    "numero" to JsonInt(101102),
                    "nome" to JsonString("Martin Fowler"),
                    "internacional" to JsonBoolean(true)
                )),
                JsonObject(mapOf(
                    "numero" to JsonInt(26503),
                    "nome" to JsonString("André Santos"),
                    "internacional" to JsonBoolean(false)
                ))
            ))
        ))

        val visitor = GetNumerosVisitor()
        course.accept(visitor)
        assertEquals(listOf(101101, 101102, 26503), visitor.getNumeros())
    }
}
class GetObjectsWithNameAndNumberVisitorTest{
    @Test
    fun testGetObjectsWithNameAndNumberVisitor() {
        val json = JsonObject(mapOf(
            "a" to JsonInt(1),
            "b" to JsonString("hello"),
            "c" to JsonObject(mapOf(
                "numero" to JsonInt(123),
                "nome" to JsonString("John Doe")
            )),
            "d" to JsonObject(mapOf(
                "x" to JsonDouble(3.14),
                "y" to JsonBoolean(true)
            )),
            "e" to JsonArray(listOf(
                JsonObject(mapOf(
                    "numero" to JsonInt(456),
                    "nome" to JsonString("Jane Smith")
                )),
                JsonObject(mapOf(
                    "name" to JsonString("Bob Johnson"),
                    "numero" to JsonInt(789)
                ))
            ))
        ))

        val visitor = GetObjectsWithNameAndNumberVisitor()
        json.accept(visitor)
        val result = visitor.getObjectsWithNameAndNumberVisitor()

        assertEquals(2, result.size)
        assertTrue(result.contains("{\"numero\": 123,\"nome\": \"John Doe\"}"))
        assertTrue(result.contains("{\"numero\": 456,\"nome\": \"Jane Smith\"}"))
    }

}
class JsonObjectTest {
    @Test
    fun jsonObjectShouldReturnValidStringTest() {
        // Arrange
        val properties = mapOf(
            "name" to JsonString("John"),
            "age" to JsonInt(30),
            "isEmployed" to JsonBoolean(true)
        )
        val jsonObject = JsonObject(properties)

        // Act
        val jsonString = jsonObject.toJsonString()

        // Assert
        assertEquals("{\"name\": \"John\",\"age\": 30,\"isEmployed\": true}", jsonString)
    }

    @Test
    fun acceptShouldCallVisitAndEndVisitFunctionsOfJsonVisitorTest() {
        // Arrange
        val properties = mapOf(
            "name" to JsonString("John"),
            "age" to JsonInt(30),
            "isEmployed" to JsonBoolean(true)
        )
        val jsonObject = JsonObject(properties)
        val mockJsonVisitor = object : JsonVisitor {
            var visited = false
            var endVisited = false
            override fun visit(jsonObject: JsonObject) {
                visited = true
            }
            override fun endVisit(jsonObject: JsonObject) {
                endVisited = true
            }
        }

        // Act
        jsonObject.accept(mockJsonVisitor)

        // Assert
        assertTrue(mockJsonVisitor.visited)
        assertTrue(mockJsonVisitor.endVisited)
    }
}

class JsonArrayTest {
    @Test
    fun toJsonStringShouldReturnValidJSONStringTest() {
        // Arrange
        val elements = listOf(
            JsonString("John"),
            JsonInt(30),
            JsonBoolean(true)
        )
        val jsonArray = JsonArray(elements)

        // Act
        val jsonString = jsonArray.toJsonString()

        // Assert
        assertEquals("[\"John\",30,true]", jsonString)
    }

    @Test
    fun acceptShouldCallVisitAndEndVisitFunctionsOfJsonVisitorTest() {
        // Arrange
        val elements = listOf(
            JsonString("John"),
            JsonInt(30),
            JsonBoolean(true)
        )
        val jsonArray = JsonArray(elements)
        val mockJsonVisitor = object : JsonVisitor {
            var visited = false
            var endVisited = false
            override fun visit(jsonArray: JsonArray) {
                visited = true
            }
            override fun endVisit(jsonArray: JsonArray) {
                endVisited = true
            }
        }

        // Act
        jsonArray.accept(mockJsonVisitor)

        // Assert
        assertTrue(mockJsonVisitor.visited)
        assertTrue(mockJsonVisitor.endVisited)
    }
}

class JsonIntTest {
    @Test
    fun toJsonStringShouldReturnValidJSONStringTest() {
        // Arrange
        val jsonInt = JsonInt(42)

        // Act
        val jsonString = jsonInt.toJsonString()

        // Assert
        assertEquals("42", jsonString)
    }

    @Test
    fun acceptShouldCallVisitFunctionOfJsonVisitorTest() {
        // Arrange
        val jsonInt = JsonInt(42)
        val mockJsonVisitor = object : JsonVisitor {
            var visited = false
            override fun visit(jsonInt: JsonInt) {
                visited = true
            }
        }

        // Act
        jsonInt.accept(mockJsonVisitor)

        // Assert
        assertTrue(mockJsonVisitor.visited)
    }
}

class JsonDoubleTest {
    @Test
    fun toJsonStringShouldReturnValidJSONStringTest() {
        // Arrange
        val jsonDouble = JsonDouble(3.14)

        // Act
        val jsonString = jsonDouble.toJsonString()

        // Assert
        assertEquals("3.14", jsonString)
    }

    @Test
    fun acceptShouldCallVisitFunctionOfJsonVisitorTest() {
        // Arrange
        val jsonDouble = JsonDouble(3.14)
        val mockJsonVisitor = object : JsonVisitor {
            var visited = false
            override fun visit(jsonDouble: JsonDouble) {
                visited = true
            }
        }

        // Act
        jsonDouble.accept(mockJsonVisitor)

        // Assert
        assertTrue(mockJsonVisitor.visited)
    }
}

class JsonStringTest {
    @Test
    fun toJsonStringShouldReturnValidJSONStringTest() {
        // Arrange
        val jsonString = "Hello, world!"
        val jsonStr = JsonString(jsonString)

        // Act
        val result = jsonStr.toJsonString()

        // Assert
        assertEquals("\"$jsonString\"", result)
    }

    @Test
    fun acceptShouldCallVisitFunctionOfJsonVisitorTest() {
        // Arrange
        val jsonString = "Hello, world!"
        val jsonStr = JsonString(jsonString)
        val mockJsonVisitor = object : JsonVisitor {
            var visited = false
            override fun visit(jsonString: JsonString) {
                visited = true
            }
        }

        // Act
        jsonStr.accept(mockJsonVisitor)

        // Assert
        assertTrue(mockJsonVisitor.visited)
    }
}

class JsonNullTest {
    @Test
    fun toJsonStringShouldReturnValidJSONStringTest() {
        // Arrange
        val jsonNull = JsonNull()

        // Act
        val jsonString = jsonNull.toJsonString()

        // Assert
        assertEquals("null", jsonString)
    }

    @Test
    fun acceptShouldCallVisitFunctionOfJsonVisitorTest() {
        // Arrange
        val jsonNull = JsonNull()
        val mockJsonVisitor = object : JsonVisitor {
            var visited = false
            override fun visit(jsonNull: JsonNull) {
                visited = true
            }
        }

        // Act
        jsonNull.accept(mockJsonVisitor)

        // Assert
        assertTrue(mockJsonVisitor.visited)
    }
}

class JsonBooleanTest {
    @Test
    fun toJsonStringShouldReturnvalidJSONstringTest() {
        // Arrange
        val jsonBoolean = JsonBoolean(true)

        // Act
        val jsonString = jsonBoolean.toJsonString()

        // Assert
        assertEquals("true", jsonString)
    }

    @Test
    fun acceptShouldCallVisitFunctionOfJsonVisitorTest() {
        // Arrange
        val jsonBoolean = JsonBoolean(true)
        val mockJsonVisitor = object : JsonVisitor {
            var visited = false
            override fun visit(jsonBoolean: JsonBoolean) {
                visited = true
            }

        }
        // Act
        jsonBoolean.accept(mockJsonVisitor)

        // Assert
        assertTrue(mockJsonVisitor.visited)
    }
}
fun main(){
    val student1 = Student(12345, "Ruben Bento", false)
    val student2 = Student(67890, "Gonçalo Pereira", false)
    val list = listOf<Student>(student1, student2)
    val exam = Exam("Teste", 6.0, null, list)

    val jsonObject = toJson(exam)
    println(jsonObject.toJsonString())
}


data class Exam(
    val uc: String,
    val ects: Double,
    @UseAsString
    val dataExame: Int? = null,
    val incritos: List<Student>
)

data class Student(
    val numero: Int,
    @PropertyName("nome-aluno")
    val nome: String,
    @Ignore
    val internacional: Boolean,
)
