Os visitors são uma forma flexível de percorrer e processar estruturas de dados complexas, como árvores hierárquicas de valores JSON. Neste documento está explicado como usar visitors existentes e criar um novo visitor para realizar ações específicas em uma estrutura JSON.
### Usar um visitor existente
Os visitors existentes, como GetNumerosVisitor, GetObjectsWithNameAndNumberVisitor, VerifyStructureVisitor e VerifyInscritosVisitor, já estão implementados e podem ser usados diretamente.
Para usar um visitor existente, deve criar uma instância do visitor desejado.
Cahmar o método accept no objeto JSON passando o visitor como argumento.
Utilizar os métodos específicos do visitor para obter os resultados desejados.

Exemplo de uso do GetNumerosVisitor para obter uma lista de números de um objeto JSON:

````kotlin
val jsonObject = JsonObject(mapOf("numero" to JsonInt(42), "texto" to JsonString("Olá, mundo!")))

val getNumerosVisitor = GetNumerosVisitor()
jsonObject.accept(getNumerosVisitor)

val numeros = getNumerosVisitor.getNumeros()
println("Números encontrados: $numeros") // Números encontrados: 42
````
### Criando um novo visitor

Para criar um novo visitor, deve implementar a interface JsonVisitor.
Forneçer a implementação dos métodos visit correspondentes a cada tipo de nó JSON.
Dentro dos métodos visit, realizar as ações desejadas no nó específico.
Utilizar o novo visitor da mesma forma que os visitors existentes.

Exemplo de criação de um novo visitor chamado MyVisitor que imprime os valores dos nós JSON:

````kotlin
class MyVisitor : JsonVisitor {
    override fun visit(jsonObject: JsonObject) {
    println("Visiting JsonObject")
    // Realize as ações desejadas no JsonObject
    }

    override fun visit(jsonArray: JsonArray) {
        println("Visiting JsonArray")
        // Realize as ações desejadas no JsonArray
    }

    override fun visit(jsonInt: JsonInt) {
        println("Visiting JsonInt: ${jsonInt.value}")
        // Realize as ações desejadas no JsonInt
    }

    // Implemente os outros métodos de visita conforme necessário
}
````
}