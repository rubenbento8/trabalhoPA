# Projeto PA - Editor JSON
O projeto editor JSON é uma biblioteca desenvolvida para facilitar a criação, manipulação e conversão de estruturas JSON em Kotlin. Com essa biblioteca é possível criar objetos JSON de forma simples e convertê-los em strings JSON.

## Autores
O projeto foi desenvolvido por Gonçalo Pereira, 92973, e Rúben Bento, 88047.

## Estrutura de ficheiros
A estrutura de arquivos do projeto é organizada da seguinte maneira:

- bin/
- .gradle/
- .idea/
- .vscode/
- grade/
- build/
- src/
    - main/
        - kotlin/ 
          - Helpers.kt
          - Main.kt
          - Test.kt
- docs/
    - anotations.md
    - simple.md
    - visitors.md
- README.md  

A pasta src contém o código-fonte principal do projeto. Os arquivos principais são:
* Main.kt - Possui toda a parte funcional da biblioteca e é o cerne de todo o projeto
* Helpers.kt - Possui toda a parte funcional da interface gráfica.
* Test.kt . Possui todos os testes utilizados para certificar o funcionamento da biblioteca.

A pasta tutorials contém os tutoriais relacionados à biblioteca JSON-Kotlin. Você pode encontrá-los nos arquivos TutorialSimples.md e TutorialCompleto.md.

## Tutoriais
Para aprender a usar a biblioteca editor JSON, consulte os seguintes tutoriais:

* [Simples](https://github.com/rubenbento8/trabalhoPA/blob/main/docs/simple.md) : Este tutorial explica como usar a classe JsonValue e criar estruturas JSON básicas.
* [Visitantes](https://github.com/rubenbento8/trabalhoPA/blob/main/docs/visitors.md) : Este tutorial explica como usar visitantes já existentes e como criar e usar novos.
* [Anotações](https://github.com/rubenbento8/trabalhoPA/blob/main/docs/anotations.md) : Este tutorial explica que anotações existem e como as usar.
