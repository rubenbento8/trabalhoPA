import java.util.*

fun main() {
    val obj = JsonObject(
        mapOf(
            "uc" to JsonString("PA"),
            "ects" to JsonNumber(6.0),
            "data-exame" to JsonNull(),
            "inscritos" to JsonArray(
                listOf(
                    JsonObject(
                        mapOf(
                            "numero" to JsonNumber(101101),
                            "nome" to JsonString("Dave Farley"),
                            "internacional" to JsonBoolean(true)
                        )
                    ),
                    JsonObject(
                        mapOf(
                            "numero" to JsonNumber(101102),
                            "nome" to JsonString("Martin Fowler"),
                            "internacional" to JsonBoolean(true)
                        )
                    ),
                    JsonObject(
                        mapOf(
                            "numero" to JsonNumber(26503),
                            "nome" to JsonString("André Santos"),
                            "internacional" to JsonBoolean(false)
                        )
                    )
                )
            )
        )
    )

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