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
}