Neste documento está explicado que anotações existem e como as usar na classe JsonValue para personalizar o comportamento de serialização e desserialização de objetos.

* Anotação @PropertyName:
A anotação PropertyName é usada para especificar o nome de propriedades em objetos JSON. Ela é aplicada em propriedades de uma classe de dados e permite definir um nome personalizado para a propriedade no JSON.

    Exemplo de uso:

    ````kotlin
    data class Person(
        @PropertyName("nome")
        val name: String,
    
        @PropertyName("idade")
        val age: Int
    )
    ````

* Anotação @Ignore:
A anotação Ignore é usada para indicar que uma propriedade não deve ser incluída na serialização ou desserialização JSON. É útil desejar excluir certas propriedades do objeto JSON.

    Exemplo de uso:
    ````kotlin
    data class Person(
        val name: String,
    
        @Ignore
        val sensitiveData: String
    )
    ````

* Anotação @UseAsString:
A anotação UseAsString é usada para forçar a serialização de uma propriedade como uma string no JSON, independentemente do tipo de dado real da propriedade.

    Exemplo de uso:
    
    ````kotlin
    data class Person(
        val name: String,
        @UseAsString
        val age: Int
    )
    ````

* Uso das anotações:
As anotações podem ser usadas em conjunto para personalizar a serialização e desserialização de objetos JSON. Ao chamar a função toJson, as anotações são consideradas para determinar o comportamento de serialização.

    Exemplo de uso:
    
    ````kotlin
    val person = Person("John Doe", 30)
    val jsonObject = toJson(person)
    println(jsonObject.toJsonString()) // {"nome": "John Doe", "idade": "30"}
    ````
