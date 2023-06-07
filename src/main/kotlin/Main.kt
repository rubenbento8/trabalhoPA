import kotlin.reflect.*
import kotlin.reflect.full.*

interface JsonValue {
    fun toJsonString(): String
    fun accept(jsonVisitor: JsonVisitor)
}

class JsonObject(val properties: MutableMap<String, JsonValue>) : JsonValue {
    override fun toJsonString(): String {
        return buildString {
            append("{\n")
            properties.entries.joinTo(this, ",\n") { (key, value) ->
                "\"$key\": ${value.toJsonString()}"
            }
            //append("\n")
            append("\n}\n")
        }
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
        properties.forEach {
            it.value.accept(jsonVisitor)
        }
        jsonVisitor.endVisit(this)
    }
}

class JsonArray(val elements: MutableList<JsonValue>) : JsonValue {
    override fun toJsonString(): String {
        return buildString {
            append("[\n")
            elements.joinTo(this, ",\n") { it.toJsonString() }
            append("]")
        }
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
        elements.forEach {
            it.accept(jsonVisitor)
        }
        jsonVisitor.endVisit(this)
    }
}

class JsonInt(val value: Int) : JsonValue {
    override fun toJsonString(): String {
        return value.toString()
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
    }

}

class JsonDouble(private val value: Double) : JsonValue {
    override fun toJsonString(): String {
        return value.toString()
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
    }
}

class JsonString(private val value: String) : JsonValue {
    override fun toJsonString(): String {
        return "\"$value\""
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
    }
}

class JsonBoolean(private val value: Boolean) : JsonValue {
    override fun toJsonString(): String {
        return value.toString()
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
    }
}

class JsonNull : JsonValue {
    override fun toJsonString(): String {
        return "null"
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
    }
}


interface JsonVisitor {
    fun visit(jsonObject: JsonObject) {}
    fun endVisit(jsonObject: JsonObject) {}
    fun visit(jsonArray: JsonArray) {}
    fun endVisit(jsonArray: JsonArray) {}
    fun visit(jsonInt: JsonInt) {}
    fun visit(jsonDouble: JsonDouble) {}
    fun visit(jsonString: JsonString) {}
    fun visit(jsonBoolean: JsonBoolean) {}
    fun visit(jsonNull: JsonNull) {}
    fun getNumeros(): List<Int> {
        return emptyList()
    }

    fun getObjectsWithNameAndNumberVisitor(): List<String> {
        return emptyList()
    }

    fun isValidStructure(): Boolean {
        return true
    }

    fun verifyInscritosVisitor(): Boolean {
        return true
    }
}

@Target(AnnotationTarget.PROPERTY)
annotation class PropertyName(val value: String)

@Target(AnnotationTarget.PROPERTY)
annotation class Ignore

@Target(AnnotationTarget.PROPERTY)
annotation class UseAsString


fun toJson(inputR: Any?): JsonValue {
    if (inputR != null) {
        when (inputR) {
            is Int -> {
                return JsonInt(inputR)
            }

            is Double -> {
                return JsonDouble(inputR)
            }

            is String -> {
                return JsonString(inputR)
            }

            is Boolean -> {
                return JsonBoolean(inputR)
            }

            is Map<*, *> -> {
                val map = mutableMapOf<String, JsonValue>()
                inputR.forEach { (key, value) ->
                    map[key as String] = toJson(value!!)
                }
                return JsonObject(map)
            }

            is Collection<*> -> {
                val list = mutableListOf<JsonValue>()
                inputR.forEach {
                    list.add(toJson(it!!))
                }
                return JsonArray(list)
            }

            else -> {
                val p = inputR::class
                val ifIsEnum = p as KClass<*>
                if (ifIsEnum.isEnum) {
                    return JsonString(inputR.toString())
                } else {

                    val clazz = inputR::class
                    val map = mutableMapOf<String, JsonValue>()

                    clazz.dataClassFields.forEach { prop ->
                        if (!prop.hasAnnotation<Ignore>()) {
                            when (prop.getType()) {
                                "Int" ->{
                                    map[prop.getName()] = toJson(prop.call(inputR) as Int)
                                }

                                "String" -> {
                                    map[prop.getName()] = toJson(prop.call(inputR).toString())
                                }

                                "Boolean" -> {
                                    map[prop.getName()] = toJson(prop.call(inputR) as Boolean)
                                }

                                "Double" -> {
                                    map[prop.getName()] = toJson(prop.call(inputR) as Double)
                                }

                                else -> {
                                    map[prop.getName()] = toJson(prop.call(inputR))
                                }
                            }
                        }
                    }
                    return JsonObject(map)
                }
            }
        }
    }
    return JsonNull()
}

