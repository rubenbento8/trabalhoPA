import java.awt.Component
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.*
import javax.swing.*
import java.util.Stack

fun main() {

    val student1 = Student(12345, "Ruben Bento", false, StudentType.Master)
    val student2 = Student(67890, "Gonçalo Pereira", false, StudentType.Master)
    val list = listOf<Student>(student1, student2)
    val exam = Exam("Teste", 6.0, null, list, student1)

    var editor = Editor(exam)
    editor.open()
}

class Editor(var objectz: Any) {

    private var commandList: Stack<Command> = Stack<Command>()
    private var jsonObject: JsonObject = toJson(objectz) as JsonObject
    private val jsonObjectMap: MutableMap<String, JsonValue> = jsonObject.properties as MutableMap<String, JsonValue>
    private val panelList: MutableList<JPanel> = mutableListOf<JPanel>()
    private var scrollPane: JScrollPane
    private val srcArea: JTextArea

    val frame = JFrame("JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 2)
        size = Dimension(600, 600)

        val left = JPanel()
        left.layout = GridLayout()
        scrollPane = JScrollPane(RootPanel()).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }
        left.add(scrollPane)
        add(left)

        val right = JPanel()
        right.layout = GridLayout()
        srcArea = JTextArea()
        srcArea.tabSize = 2
        srcArea.text = jsonObject.toJsonString()
        right.add(srcArea)
        add(right)
    }

    fun open() {
        frame.isVisible = true
    }

    fun RootPanel(): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            jsonObject.properties.forEach {
                when(it.value){
                    is JsonArray -> {
                        add(arrayPrepWidget(it.key, it.value as JsonArray))
                        println("Array")
                    }
                    is JsonObject -> {
                        add(objectPrepWidget(it.key,  it.value as JsonObject))
                        println("Object")
                    }
                    else ->{
                        add(propertyWidget(it.key,  it.value.toJsonString().removeSurrounding("\"")))
                    }
                }
            }

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val add = JButton("Add Property")
                        add.addActionListener {
                            val text = JOptionPane.showInputDialog("Property Name")
                            menu.isVisible = false

                            if(!jsonObjectMap.containsKey(text)) {
                                val command = AddProperty(this@apply, text)
                                commandList.add(command)
                                command.execute()
                            }
                        }
                        val undo = JButton("Undo")
                        undo.addActionListener {
                            if(commandList.isNotEmpty()) {
                                val command = commandList.pop()
                                command.undo()
                            }
                            menu.isVisible = false
                        }

                        val del = JButton("Delete all")
                        del.addActionListener {
                            components.forEach {
                                remove(it)
                            }
                            menu.isVisible = false
                            revalidate()
                            frame.repaint()
                        }
                        menu.add(add)
                        menu.add(undo)
                        menu.add(del)
                        menu.show(this@apply, 100, 100)
                    }
                }
            })
        }

    fun arrayWidget(value: String): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            val text = JTextField(value)
            text.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    //jsonObjectMap[key] = it
                    //jsonObject = JsonObject(jsonObjectMap)
                    //srcArea.text = jsonObject.toJsonString()
                    //frame.repaint()
                }
            })
            add(text)
        }

    fun arrayPrepWidget(key: String, array: JsonArray): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(JLabel(" " + key))
            add(arrayPanel(array))
        }

    fun arrayPanel(array: JsonArray): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            var arrayF = array

            add(JLabel(" "))
            array.elements.forEach { it ->
                add(arrayWidget(it.toJsonString().removeSurrounding("\"")))
            }

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val add = JButton("Add Array Element Element")
                        add.addActionListener {
                            val command = AddArrayElement(this@apply, arrayF)
                            commandList.add(command)
                            command.execute()
                            arrayF = command.newJsonArray
                            menu.isVisible = false
                        }
                        val del = JButton("delete all")
                        del.addActionListener {
                            components.forEach {
                                remove(it)
                            }
                            menu.isVisible = false
                            revalidate()
                            frame.repaint()
                        }
                        menu.add(add)
                        menu.add(del)
                        menu.show(this@apply, 200, 250)
                    }
                }
            })
        }

    fun objectPrepWidget(key: String, objectR: JsonObject): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(JLabel(" " + key))
            add(objectPanel(objectR))
        }

    fun objectPanel(objectR: JsonObject): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            objectR.properties.forEach {
                when(it.value){
                    is JsonArray -> {
                        add(arrayPrepWidget(it.key, it.value as JsonArray))
                        println("Array")
                    }
                    is JsonObject -> {
                        add(objectPrepWidget(it.key,  it.value as JsonObject))
                        println("Object")
                    }
                    else ->{
                        add(propertyWidget(it.key,  it.value.toJsonString().removeSurrounding("\"")))
                    }
                }
            }

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val add = JButton("Add Property")
                        add.addActionListener {
                            val text = JOptionPane.showInputDialog("Property Name")
                            menu.isVisible = false

                            if(!jsonObjectMap.containsKey(text)) {
                                val command = AddProperty(this@apply, text)
                                commandList.add(command)
                                command.execute()
                            }
                        }
                        val undo = JButton("Undo")
                        undo.addActionListener {
                            if(commandList.isNotEmpty()) {
                                val command = commandList.pop()
                                command.undo()
                            }
                            menu.isVisible = false
                        }

                        val del = JButton("Delete all")
                        del.addActionListener {
                            components.forEach {
                                remove(it)
                            }
                            menu.isVisible = false
                            revalidate()
                            frame.repaint()
                        }
                        menu.add(add)
                        menu.add(undo)
                        menu.add(del)
                        menu.show(this@apply, 100, 100)
                    }
                }
            })
        }

    fun propertyWidget(key: String, value: String): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(JLabel(" " + key))
            val text = JTextField(value)

            // menu
            text.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val del = JButton("delete")
                        del.addActionListener {
                            remove(this@apply)
                            menu.isVisible = false
                        }
                        revalidate()
                        frame.repaint()
                        menu.add(del)
                        menu.show(this@apply, 200, 250)
                    }
                }
            })

            // edit
            text.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    if(!jsonObjectMap.get(key)!!.toJsonString().removeSurrounding("\"").equals(text.text)){
                        val command = EditProperty(this@apply, key, text.text, text)
                        commandList.add(command)
                        command.execute()
                    }
                }
            })
            add(text)
        }

    interface Command {
        fun execute()
        fun undo()
    }

    inner class AddProperty(var panel: JPanel, var key: String) : Command {
        override fun execute() {
                panel.add(propertyWidget(key, "null"))
                jsonObjectMap[key] = toJson(null)
                jsonObject = JsonObject(jsonObjectMap)
                srcArea.text = jsonObject.toJsonString()
                panel.revalidate()
                frame.repaint()
        }
        override fun undo(){}
    }

    inner class EditProperty(var panel: JPanel, var key: String, var value: String, var jTextField: JTextField) : Command {
        var oldValue: JsonValue = jsonObjectMap.get(key)!!

        override fun execute() {
            jsonObjectMap[key] = toJson(parseValue(value))
            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.revalidate()
            frame.repaint()
        }
        override fun undo(){
            jTextField.text = oldValue.toJsonString().removeSurrounding("\"")
            jsonObjectMap[key] = oldValue
            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.revalidate()
            frame.repaint()
        }
    }

    inner class AddArrayElement(var panel: JPanel, var array: JsonArray) : Command {
        val oldJsonArray = array
        var newJsonArray = array

        override fun execute() {
            var k = getKeyByValue(jsonObjectMap, array) as String
            var newArray: MutableList<JsonValue> = array.elements.toMutableList()
            newArray.add(JsonNull())
            newJsonArray = JsonArray(newArray as List<JsonValue>)
            jsonObjectMap[k] = newJsonArray
            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.add(arrayWidget("null"))
            panel.revalidate()
            frame.repaint()
        }
        override fun undo(){}
    }


}

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

fun <String, JsonValue> getKeyByValue(map: Map<String, JsonValue>, value: JsonValue): String? {
    return map.entries.find { it.value == value }?.key
}
