import javax.swing.*
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

val getTypeMap = mapOf<KType, String>(
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

sealed class MapOrJPanel(open val parent: MapOrJPanel.Map? = null) {
    data class Panel(override val parent: MapOrJPanel.Map?, val jPanel: JPanel) : MapOrJPanel(parent)
    data class Map(override val parent: MapOrJPanel.Map? = null, val map: MutableMap<String, MapOrJPanel> = mutableMapOf()) : MapOrJPanel(parent)

    // Add a child to a map, setting the map as the child's parent
    fun Map.addChild(key: String, child: MapOrJPanel) {
        when (child) {
            is MapOrJPanel.Panel -> map[key] = MapOrJPanel.Panel(this, child.jPanel)
            is MapOrJPanel.Map -> map[key] = MapOrJPanel.Map(this, child.map)
        }
    }

    fun handle(mapOrJPanel: MapOrJPanel) {
        when(mapOrJPanel) {
            is MapOrJPanel.Panel -> {
                // Do something with mapOrJPanel.jPanel
            }
            is MapOrJPanel.Map -> {
                // Do something with mapOrJPanel.map
            }
        }
    }
}

typealias NestedMap = MutableMap<String, JsonValue>

fun addValueToNestedMap(nestedMap: NestedMap, keys: List<String>, newValue: JsonValue) {
    var currentMap = nestedMap
    var newValueKey = keys.last()
    var currentArray = mutableListOf<JsonValue>()

    for (key in keys) {
        if(!isStringInt(key)) {
            if (!key.equals(keys.last())) {
                if (currentMap.get(key) is JsonObject) {
                    val value = currentMap.get(key) as JsonObject
                    currentMap = value.properties as NestedMap
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements.toMutableList()
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties as NestedMap
        }
    }

    currentMap[newValueKey] = newValue
}

fun removeValueToNestedMap(nestedMap: NestedMap, keys: List<String>) {
    var currentMap = nestedMap
    var newValueKey = keys.last()
    var currentArray = mutableListOf<JsonValue>()

    for (key in keys) {
        if(!isStringInt(key)) {
            if (!key.equals(keys.last())) {
                if (currentMap.get(key) is JsonObject) {
                    val value = currentMap.get(key) as JsonObject
                    currentMap = value.properties as NestedMap
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements.toMutableList()
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties as NestedMap
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
                    currentMap = value.properties as NestedMap
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements.toMutableList()
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties as NestedMap
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
                    currentMap = value.properties as NestedMap
                }
                else if(currentMap.get(key) is JsonArray){
                    val value = currentMap.get(key) as JsonArray
                    currentArray = value.elements.toMutableList()
                }
            }
        }
        else{
            val value = currentArray.get(key.toInt()) as JsonObject
            currentMap = value.properties as NestedMap
        }
    }
    return(currentMap.get(keys.last())!!)
}

fun <String, JsonValue> getKeyByValue(map: Map<String, JsonValue>, value: JsonValue): String? {
    return map.entries.find { it.value == value }?.key
}

