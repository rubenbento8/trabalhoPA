import java.awt.Component
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.*
import javax.swing.*
import java.util.Stack

fun main() {

    val student1 = Student(12345, "Ruben Bento", false, StudentType.Master)
    val student2 = Student(67890, "Gonçalo Pereira", false, StudentType.Master)
    val list = listOf(student1, student2)
    val exam = Exam("Teste", 6.0, null, list, student1)

    val editor = Editor(exam)
    editor.open()
}

class Editor(objectz: Any) {

    private var commandList: Stack<Command> = Stack<Command>()
    private var jsonObject: JsonObject = toJson(objectz) as JsonObject
    private var jsonObjectMap: MutableMap<String, JsonValue> = jsonObject.properties
    private var rootPanel: JScrollPane
    private var srcArea: JTextArea
    private var left: JPanel
    private var right: JPanel


    val frame = JFrame("JSON Object Editor").apply {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        layout = GridLayout(0, 2)
        size = Dimension(600, 600)

        left = JPanel()
        left.layout = GridLayout()
        rootPanel = JScrollPane(RootPanel()).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }
        left.add(rootPanel)
        add(left)

        right = JPanel()
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

    fun resetFrame(){
        frame.remove(left)

        left = JPanel()
        left.layout = GridLayout()
        rootPanel = JScrollPane(RootPanel()).apply {
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
        }
        left.add(rootPanel)
        frame.add(left)

        frame.remove(right)

        right = JPanel()
        right.layout = GridLayout()
        srcArea = JTextArea()
        srcArea.tabSize = 2
        srcArea.text = jsonObject.toJsonString()
        right.add(srcArea)
        frame.add(right)

        frame.revalidate()
        frame.repaint()
    }

    private fun RootPanel(): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            jsonObject.properties.forEach {
                when(it.value){
                    is JsonArray -> {
                        add(arrayPrepWidget(it.key, it.value as JsonArray))
                    }
                    is JsonObject -> {
                        add(objectPrepWidget(mutableListOf(it.key),  it.value as JsonObject))
                    }
                    else ->{
                        add(propertyWidget(mutableListOf(it.key),  it.value.toJsonString().removeSurrounding("\"")))
                    }
                }
            }

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val addPro = JButton("Add Property")
                        addPro.addActionListener {
                            val text = JOptionPane.showInputDialog("Property Name")
                            menu.isVisible = false

                            if(!jsonObjectMap.containsKey(text)) {
                                val command = AddProperty(this@apply, mutableListOf(text))
                                commandList.add(command)
                                command.execute()
                            }
                        }

                        val addObj = JButton("Add Object")
                        addObj.addActionListener {
                            val objName = JOptionPane.showInputDialog("Object Name")
                            val propList = mutableListOf<String>()
                            var done = true

                            while(done){
                                val propName = JOptionPane.showInputDialog("Property Name (Enter \"?\" when finished)")
                                if(!propName.equals("?")){
                                    propList.add(propName)
                                }
                                else{
                                    done = false
                                }
                            }

                            menu.isVisible = false
                            val listaKeys = mutableListOf<String>(objName)
                            val command = AddObject(this@apply, listaKeys, propList)
                            commandList.add(command)
                            command.execute()
                            menu.isVisible = false
                        }
                        val undo = JButton("Undo")
                        undo.addActionListener {
                            if(commandList.isNotEmpty()) {
                                val command = commandList.pop()
                                command.undo()
                            }
                            menu.isVisible = false
                        }

                        val delAll = JButton("Delete all")
                        delAll.addActionListener {

                            val command = ResetObject()
                            commandList.add(command)
                            command.execute()
                            menu.isVisible = false
                        }
                        menu.add(addPro)
                        menu.add(addObj)
                        menu.add(undo)
                        menu.add(delAll)
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

    private fun arrayPrepWidget(key: String, array: JsonArray): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(JLabel(" $key"))
            add(arrayPanel(key, array))
        }

    private fun arrayPanel(key: String, array: JsonArray): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            var arrayF = array

            add(JLabel(" "))
            var i = 0
            array.elements.forEach {
                when(it){
                    is JsonObject -> {
                        add(objectPrepWidget(mutableListOf(key, i.toString()), it))
                    }
                    else ->{
                        add(arrayWidget(it.toJsonString().removeSurrounding("\"")))
                    }
                }
                i++
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

    fun objectPrepWidget(listKeys: MutableList<String>, objectR: JsonObject): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(JLabel("   " + listKeys.last()))
            add(objectPanel(listKeys, objectR))
        }

    private fun objectPanel(listKeys: MutableList<String>, objectR: JsonObject): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            objectR.properties.forEach {
                when(it.value){
                    is JsonArray -> {
                        add(arrayPrepWidget(it.key, it.value as JsonArray))
                    }
                    is JsonObject -> {
                        add(objectPrepWidget(listKeys,  it.value as JsonObject))
                    }
                    else -> {
                        val listaKeys = listKeys.toMutableList()
                        listaKeys.add(it.key)
                        add(propertyWidget(listaKeys, it.value.toJsonString().removeSurrounding("\"")))
                    }
                }
            }

            // menu
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val addPro = JButton("Add Property")
                        addPro.addActionListener {
                            val text = JOptionPane.showInputDialog("Property Name")
                            menu.isVisible = false

                            if(!jsonObjectMap.containsKey(text)) {
                                listKeys.add(text)
                                val command = AddProperty(this@apply, listKeys)
                                commandList.add(command)
                                command.execute()
                            }
                        }
                        val addObj = JButton("Add Object")
                        addObj.addActionListener {
                            val objName = JOptionPane.showInputDialog("Object Name")
                            val propList = mutableListOf<String>()
                            var done = true

                            while(done){
                                val propName = JOptionPane.showInputDialog("Property Name (Enter \"?\" when finished)")
                                if(!propName.equals("?")){
                                    if(!propList.contains(propName)) {
                                        propList.add(propName)
                                    }
                                }
                                else{
                                    done = false
                                }
                            }
                            menu.isVisible = false

                            val listaKeys = listKeys.toMutableList()
                            listaKeys.add(objName)
                            val command = AddObject(this@apply, listaKeys, propList)
                            commandList.add(command)
                            command.execute()
                            menu.isVisible = false
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
                        menu.add(addPro)
                        menu.add(addObj)
                        menu.add(undo)
                        menu.add(del)
                        menu.show(this@apply, 100, 100)
                    }
                }
            })
        }

    fun propertyWidget(listKeys: MutableList<String>, value: String): JPanel =
        JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.TOP_ALIGNMENT

            add(JLabel(" " + listKeys.last()))
            val text = JTextField(value)

            // menu
            text.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        val menu = JPopupMenu("Message")
                        val del = JButton("Delete")
                        del.addActionListener {

                            val command = DeleteProperty(listKeys)
                            commandList.add(command)
                            command.execute()

                            menu.isVisible = false
                        }
                        val undo = JButton("Undo")
                        undo.addActionListener {
                            if(commandList.isNotEmpty()) {
                                val command = commandList.pop()
                                command.undo()
                            }
                            menu.isVisible = false
                        }

                        revalidate()
                        frame.repaint()
                        menu.add(del)
                        menu.add(undo)
                        menu.show(this@apply, 200, 250)
                    }
                }

            })

            // edit
            text.addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent) {
                    if (checkIfValueDoesntChange(jsonObjectMap, listKeys, text.text)){
                        val command = EditProperty(this@apply, listKeys, text.text, text)
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

    inner class AddProperty(private var panel: JPanel, private var keys: MutableList<String>) : Command {
        private var oldValue = jsonObjectMap.toMutableMap()

        override fun execute() {
            panel.add(propertyWidget(keys, "null"))
            addValueToNestedMap(jsonObjectMap, keys, toJson(null))
            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.revalidate()
            frame.repaint()
        }
        override fun undo(){
            jsonObjectMap = oldValue
            jsonObject = JsonObject(jsonObjectMap)

            resetFrame()
        }
    }

    inner class DeleteProperty(private var keys: MutableList<String>) : Command {
        private var oldValue = jsonObjectMap.toMutableMap()

        override fun execute() {
            removeValueToNestedMap(jsonObjectMap, keys)
            jsonObject = JsonObject(jsonObjectMap)

            resetFrame()
        }
        override fun undo(){
            jsonObjectMap = oldValue
            jsonObject = JsonObject(jsonObjectMap)

            resetFrame()
        }
    }

    inner class ResetObject() : Command {
        private var oldValue = jsonObjectMap.toMutableMap()

        override fun execute() {
            jsonObjectMap = mutableMapOf()
            jsonObject = JsonObject(jsonObjectMap)

            resetFrame()
        }
        override fun undo(){
            jsonObjectMap = oldValue
            jsonObject = JsonObject(jsonObjectMap)

            resetFrame()
        }
    }

    inner class AddObject(private var panel: JPanel, private var keys: MutableList<String>, private var properties: MutableList<String>) : Command {
        private var oldValue = jsonObjectMap.toMutableMap()

        override fun execute() {
            val newObjectMap = mutableMapOf<String, JsonValue>()
            properties.forEach {
                newObjectMap[it] = JsonNull()
            }
            val objR = JsonObject(newObjectMap)
            panel.add(objectPrepWidget(keys, objR))
            addValueToNestedMap(jsonObjectMap, keys, objR)

            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.revalidate()
            frame.repaint()
        }

        override fun undo(){
            jsonObjectMap = oldValue
            jsonObject = JsonObject(jsonObjectMap)

            resetFrame()
        }
    }

    inner class EditProperty(private var panel: JPanel, private var keys: MutableList<String>, private var value: String, private var jTextField: JTextField) : Command {
        private var oldValue: JsonValue = getLastValue(jsonObjectMap, keys)

        override fun execute() {
            addValueToNestedMap(jsonObjectMap, keys, toJson(parseValue(value)))
            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.revalidate()
            frame.repaint()
        }
        override fun undo(){
            jTextField.text = oldValue.toJsonString().removeSurrounding("\"")
            addValueToNestedMap(jsonObjectMap, keys, oldValue)
            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.revalidate()
            frame.repaint()
        }
    }

    inner class AddArrayElement(private var panel: JPanel, private var array: JsonArray) : Command {
        private var oldValue = jsonObjectMap.toMutableMap()
        var newJsonArray = array

        override fun execute() {
            val k = getKeyByValue(jsonObjectMap, array) as String
            val newArray: MutableList<JsonValue> = array.elements.toMutableList()
            newArray.add(JsonNull())
            newJsonArray = JsonArray(newArray)
            jsonObjectMap[k] = newJsonArray
            jsonObject = JsonObject(jsonObjectMap)
            srcArea.text = jsonObject.toJsonString()
            panel.add(arrayWidget("null"))
            panel.revalidate()
            frame.repaint()
        }
        override fun undo(){
            jsonObjectMap = oldValue
            jsonObject = JsonObject(jsonObjectMap)

            resetFrame()
        }
    }
}
