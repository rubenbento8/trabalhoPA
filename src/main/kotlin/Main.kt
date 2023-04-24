import sun.jvm.hotspot.oops.CellTypeState.value
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
        properties.forEach{
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
        elements.forEach{
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
        jsonObject.properties.forEach{
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
        if (jsonObject.properties.containsKey("numero") && jsonObject.properties.containsKey("nome")){
            objects.add(jsonObject.toJsonString())
        }
    }

    override fun getObjectsWithNameAndNumberVisitor(): List<String> {
        return objects
    }
}



interface JsonVisitor {
    fun visit(jsonObject: JsonObject){}
    fun endVisit(jsonObject: JsonObject){}
    fun visit(jsonArray: JsonArray){}
    fun endVisit(jsonArray: JsonArray){}
    fun visit(jsonInt: JsonInt){}
    fun visit(jsonDouble: JsonDouble){}
    fun visit(jsonString: JsonString){}
    fun visit(jsonBoolean: JsonBoolean){}
    fun visit(jsonNull: JsonNull){}
<<<<<<< Updated upstream
    fun getNumeros(): List<Int> {
        return emptyList()
    }
    fun getObjectsWithNameAndNumberVisitor(): List<String>{
        return emptyList()
    }
}

=======
}

@Target(AnnotationTarget.PROPERTY)
annotation class PropertyName(val value: String)

@Target(AnnotationTarget.PROPERTY)
annotation class Ignore //TODO
@Target(AnnotationTarget.PROPERTY)
annotation class UseAsString //TODO

private fun <V> KProperty<V>.getName(): String {
    if(this.hasAnnotation<PropertyName>()) {
        return this.findAnnotation<PropertyName>()!!.value
    }

    return this.name
}

fun toJson(inputR: Any): JsonValue{
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
        is Map<*,*> -> {
            val map = mutableMapOf<String, JsonValue>()
            inputR.forEach { (key, value) ->
                map[key as String] = toJson(value!!)
            }
            return JsonObject(map)
        }
        is Collection<*> -> {
            val list = mutableListOf<JsonValue>()
            inputR.forEach{it ->
                list.add(toJson(it!!))
            }
            return JsonArray(list)
        }
        else -> {
            val ifIsEnum = inputR as KClass<*>
            if (ifIsEnum.isEnum) {
                val list = mutableListOf<JsonValue>()
                inputR.enumConstants.forEach{
                    list.add(toJson(it!!))
                }
                return JsonArray(list)
            }
            else {
                val clazz = inputR::class
                val map = mutableMapOf<String, JsonValue>()

                clazz.dataClassFields.forEach { prop ->
                    when (getSQLTypeMap[prop.returnType].toString()) {
                        "Int" -> {
                            map[prop.getName()] = JsonInt(prop.call(value) as Int)
                        }

                        "String" -> {
                            map[prop.getName()] = JsonString(prop.call(value) as String)
                        }

                        "Boolean" -> {
                            map[prop.getName()] = JsonBoolean(prop.call(value) as Boolean)
                        }

                        "Double" -> {
                            map[prop.getName()] = JsonInt(prop.call(value) as Int)
                        }
                    }
                }

                return JsonObject(map)
            }
        }
    }
}

val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return this.primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
}

// saber se um KClassifier é um enumerado
val KClassifier?.isEnum: Boolean
    get() = this is KClass<*> && this.isSubclassOf(Enum::class)

// obter uma lista de constantes de um tipo enumerado
val <T : Any> KClass<T>.enumConstants: List<T> get() {
    require(isEnum) { "instance must be enum" }
    return java.enumConstants.toList()
}

val getSQLTypeMap = mapOf<KType, String>(
    String::class.createType() to "String",
    Int::class.createType() to "Int",
    Double::class.createType() to "Double",
    Boolean::class.createType() to "Boolean",
)
>>>>>>> Stashed changes
