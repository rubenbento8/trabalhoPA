import kotlin.reflect.*
import kotlin.reflect.full.*

interface JsonValue {
    fun toJsonString(): String
    fun accept(jsonVisitor: JsonVisitor)
}

class JsonObject(val properties: Map<String, JsonValue>) : JsonValue {
    override fun toJsonString(): String {
        return buildString {
            append("{")
            properties.entries.joinTo(this, ",") { (key, value) ->
                "\"$key\": ${value.toJsonString()}"
            }
            append("}")
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

class JsonArray(val elements: List<JsonValue>) : JsonValue {
    override fun toJsonString(): String {
        return buildString {
            append("[")
            elements.joinTo(this, ",") { it.toJsonString() }
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

class JsonDouble(val value: Double) : JsonValue {
    override fun toJsonString(): String {
        return value.toString()
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
    }
}

class JsonString(val value: String) : JsonValue {
    override fun toJsonString(): String {
        return "\"$value\""
    }

    override fun accept(jsonVisitor: JsonVisitor) {
        jsonVisitor.visit(this)
    }
}

class JsonBoolean(val value: Boolean) : JsonValue {
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

class GetNumerosVisitor : JsonVisitor {
    private val numbers = mutableListOf<Int>()
    override fun visit(jsonObject: JsonObject) {
        jsonObject.properties.forEach {
            if (it.key == "numero") {
                val value = it.value
                if (value is JsonInt) {
                    numbers.add(value.value)
                }
            }
        }
    }

    override fun getNumeros(): List<Int> {
        return numbers
    }

}

class GetObjectsWithNameAndNumberVisitor : JsonVisitor {
    private val objects = mutableListOf<String>()
    override fun visit(jsonObject: JsonObject) {
        if (jsonObject.properties.containsKey("numero") && jsonObject.properties.containsKey("nome")) {
            objects.add(jsonObject.toJsonString())
        }
    }

    override fun getObjectsWithNameAndNumberVisitor(): List<String> {
        return objects
    }
}


class VerifyStructureVisitor : JsonVisitor {
    private var isValid = true
    override fun visit(jsonObject: JsonObject) {
        if (jsonObject.properties.containsKey("numero")) {
            val numeroValue = jsonObject.properties["numero"]
            if (numeroValue !is JsonInt) {
                isValid = false
            }
        }
    }

    override fun isValidStructure(): Boolean {
        return isValid
    }
}

class VerifyInscritosVisitor : JsonVisitor {
    private var hasError = false
    override fun visit(jsonObject: JsonObject) {
        if (jsonObject.properties.containsKey("Inscritos")) {
            val inscritosValue = jsonObject.properties["Inscritos"]
            if (inscritosValue is JsonArray) {
                val inscritos = inscritosValue.elements
                if (inscritos.isNotEmpty()) {
                    val expectedStructure = inscritos[0]
                    for (element in inscritos) {
                        if (element !is JsonObject || expectedStructure !is JsonObject || element.properties.size != expectedStructure.properties.size) {
                            hasError = true
                            break
                        }
                    }
                }
            }
        }
    }

    override fun verifyInscritosVisitor(): Boolean {
        return hasError
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

private fun <V> KProperty<V>.getName(): String {
    if (this.hasAnnotation<PropertyName>()) {
        return this.findAnnotation<PropertyName>()!!.value
    }

    return this.name
}

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

            is Char -> {
                return JsonString(inputR as String)
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
                inputR.forEach { it ->
                    list.add(toJson(it!!))
                }
                return JsonArray(list)
            }

            else -> {
                /*
                val ifIsEnum: KClass<*> = inputR::class
            if (ifIsEnum.isEnum) {
                val list = mutableListOf<JsonValue>()
                ifIsEnum.enumConstants.forEach{
                    list.add(toJson(it!!))
                }
                return JsonArray(list)
            }
            else {
                */
                val clazz = inputR::class
                val map = mutableMapOf<String, JsonValue>()

                clazz.dataClassFields.forEach { prop ->
                    if (!prop.hasAnnotation<Ignore>()) {
                        when (prop.getType()) {
                            "Int" -> {
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
    //}
    else {
        return JsonNull()
    }
}

private val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return this.primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }

// saber se um KClassifier é um enumerado
private val KClassifier?.isEnum: Boolean
    get() = this is KClass<*> && this.isSubclassOf(Enum::class)

// obter uma lista de constantes de um tipo enumerado
private val <T : Any> KClass<T>.enumConstants: List<T>
    get() {
        require(isEnum) { "instance must be enum" }
        return java.enumConstants.toList()
    }

private val getTypeMap = mapOf<KType, String>(
    String::class.createType() to "String",
    Int::class.createType() to "Int",
    Double::class.createType() to "Double",
    Boolean::class.createType() to "Boolean",
)

private fun <V> KProperty<V>.getType(): String {
    if (this.hasAnnotation<UseAsString>()) {
        return "String"
    }
    if (getTypeMap.containsKey(this.returnType)) {
        return getTypeMap[this.returnType]!!
    }
    return "else"
}
