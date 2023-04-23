import java.util.*

fun main() {
    val obj = JsonObject(
        mapOf(
            "uc" to JsonString("PA"),
            "ects" to JsonDouble(6.0),
            "data-exame" to JsonNull(),
            "inscritos" to JsonArray(
                listOf(
                    JsonObject(
                        mapOf(
                            "numero" to JsonInt(101101),
                            "nome" to JsonString("Dave Farley"),
                            "internacional" to JsonBoolean(true)
                        )
                    ),
                    JsonObject(
                        mapOf(
                            "numero" to JsonDouble(101102.0),
                            "nome" to JsonString("Martin Fowler"),
                            "internacional" to JsonBoolean(true)
                        )
                    ),
                    JsonObject(
                        mapOf(
                            "numero" to JsonInt(26503),
                            "nome" to JsonString("André Santos"),
                            "internacional" to JsonBoolean(false)
                        )
                    )
                )
            )
        )
    )

    val lc = ValidationVisitor()
    obj.accept(lc)

    println(lc.isValid)

    println(obj.toJsonString())
}

data class Exam(
    val uc: String,
    val ects: Number,
    val dataExame: Date? = null,
    val incritos: List<Student>
)

data class Student(
    val numero: Int,
    val nome: String,
    val internacional: Boolean
)

class ValidationVisitor : JsonVisitor {
    var isValid = true

    override fun visit(obj: JsonObject) {
        if (obj.properties.containsKey("numero")) {
            if (obj.properties["numero"] !is JsonInt) {
                isValid = false
            }
        }
    }
}