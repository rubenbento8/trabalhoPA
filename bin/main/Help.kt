import kotlin.reflect.*
import kotlin.reflect.full.*

//Main Help
fun <V> KProperty<V>.getName(): String {
    if (this.hasAnnotation<PropertyName>()) {
        return this.findAnnotation<PropertyName>()!!.value
    }
    return this.name
}

val KClass<*>.dataClassFields: List<KProperty<*>>
    get() {
        require(isData) { "instance must be data class" }
        return this.primaryConstructor!!.parameters.map { p ->
            declaredMemberProperties.find { it.name == p.name }!!
        }
    }

val KClassifier?.isEnum: Boolean
    get() = this is KClass<*> && this.isSubclassOf(Enum::class)

val getTypeMap = mapOf(
    String::class.createType() to "String",
    Int::class.createType() to "Int",
    Double::class.createType() to "Double",
    Boolean::class.createType() to "Boolean",
)

fun <V> KProperty<V>.getType(): String {
    if (this.hasAnnotation<UseAsString>()) {
        return "String"
    }
    if (getTypeMap.containsKey(this.returnType)) {
        return getTypeMap[this.returnType]!!
    }
    return "else"
}


//Editor Help
fun parseValue(input: String): Any? {
    return try {
        when {
            input.toIntOrNull() != null -> input.toInt()
            input.toDoubleOrNull() != null -> input.toDouble()
            input.equals("true", ignoreCase = true) || input.equals("false", ignoreCase = true) -> input.toBoolean()
            input.equals("null", ignoreCase = true) -> null
            else -> input
        }
    } catch (e: Exception) {
        input // Return the input as string if any exception occurs during parsing
    }
}

typealias NestedMap = MutableMap<String, JsonValue>

fun addValueToNestedMap(nestedMap: NestedMap, keys: List<String>, newValue: JsonValue) {
    var currentMap = nestedMap
    val newValueKey = keys.last()
    var currentArray = mutableListOf<JsonValue>()

    for (key in keys) {
        if(!isStringInt(key)) {
            if (!key.equals(keys.last())) {
                if (currentMap.get(key) is JsonObject) {
                    val value = currentMap.get(key) as JsonObject
                    currentMap = value.properties
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties
        }
    }

    currentMap[newValueKey] = newValue
}

fun removeValueToNestedMap(nestedMap: NestedMap, keys: List<String>) {
    var currentMap = nestedMap
    val newValueKey = keys.last()
    var currentArray = mutableListOf<JsonValue>()

    for (key in keys) {
        if(!isStringInt(key)) {
            if (!key.equals(keys.last())) {
                if (currentMap.get(key) is JsonObject) {
                    val value = currentMap.get(key) as JsonObject
                    currentMap = value.properties
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties
        }
    }

    currentMap.remove(newValueKey)
}

fun checkIfValueDoesntChange(nestedMap: NestedMap, keys: List<String>, newValue: String): Boolean {
    var currentMap = nestedMap
    var currentArray = mutableListOf<JsonValue>()

    for (key in keys) {
        if(!isStringInt(key)) {
            if (!key.equals(keys.last())) {
                if (currentMap.get(key) is JsonObject) {
                    val value = currentMap.get(key) as JsonObject
                    currentMap = value.properties
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties
        }
    }

    return(!currentMap.get(keys.last())!!.toJsonString().removeSurrounding("\"").equals(newValue))
}

fun isStringInt(str: String): Boolean {
    return str.toIntOrNull() != null
}

fun getLastValue(nestedMap: NestedMap, keys: List<String>): JsonValue {
    var currentMap = nestedMap
    var currentArray = mutableListOf<JsonValue>()

    for (key in keys) {
        if(!isStringInt(key)) {
            if (!key.equals(keys.last())) {
                if (currentMap.get(key) is JsonObject) {
                    val value = currentMap.get(key) as JsonObject
                    currentMap = value.properties
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties
        }
    }
    return(currentMap.get(keys.last())!!)
}

