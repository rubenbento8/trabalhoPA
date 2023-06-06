Neste documento explicamos como utilizar a classe JsonValue. Esta classe permite criar e converter facilmente estruturas JSON em strings JSON.

### Estrutura da Classe JsonValue

A classe JsonValue é uma interface que define comportamentos comuns para os valores JSON.
As implementações da interface JsonValue, como JsonObject, JsonArray, JsonInt, JsonString, JsonBoolean e JsonNull, representam diferentes tipos de valores JSON.
Cada implementação de JsonValue possui o método toJsonString(), que retorna uma string JSON representando o valor.
Além disso, a interface JsonValue possui o método accept(), que será abordado no documento visitors.
### Criação de Estruturas JSON Usando a Classe JsonValue

Para criar uma estrutura JSON, você precisa criar instâncias das classes JsonObject, JsonArray, JsonInt, JsonString, JsonBoolean e JsonNull, conforme necessário.
Utilize os construtores dessas classes para fornecer os valores correspondentes aos elementos JSON.
Para aninhar valores JSON, utilize as classes JsonObject e JsonArray.

Por exemplo, para criar o seguinte JSON: {"nome": "John", "idade": 30}, você pode realizar o seguinte código:
````kotlin
val jsonObject = JsonObject(
    mapOf(
    "nome" to JsonString("John"),
    "idade" to JsonInt(30)))
````

### Conversão de uma Estrutura JSON em uma String JSON

Utilize o método toJsonString() de uma instância JsonValue para obter a representação em forma de string JSON do valor.
Por exemplo, para obter a representação em forma de string JSON do objeto jsonObject criado anteriormente, execute o seguinte código:
````kotlin
val jsonString = jsonObject.toJsonString()
println(jsonString) //{"nome": "John", "idade": 30}
````

### Conversão de Objetos Kotlin em Estruturas JSON

Utilize a função toJson() para converter objetos Kotlin em estruturas JSON.
A função toJson() aceita qualquer objeto Kotlin e realiza a conversão com base em seus tipos de dados.
Os tipos de dados suportados incluem Int, Double, String, Char, Boolean, Map, Collection e enumerações.
Os campos de dados de um objeto Kotlin serão convertidos em propriedades JSON.

Por exemplo, considere a seguinte classe Kotlin:
````kotlin
data class Person(
    val nome: String,
    val idade: Int,
    val email: String
)
````
Para converter um objeto Person em uma estrutura JSON, siga estes passos:

````kotlin
val person = Person("John", 30, "john@example.com")
val jsonPerson = toJson(person)
val jsonString = jsonPerson.toJsonString()
println(jsonString) // {"nome": "John", "idade": 30, "email": john@example.com"}
````
