
<h1>HubStock</h1> 

![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

O HubStock API foi desenvolvido para auxiliar no gerenciamento de inventário da sua empresa.

## 🔨 Funcionalidades do projeto
A API realiza o cadastro, transferência e pesquisa de itens. Além de opções para realizar baixa do item e geração de relatórios de movimentação e de alteração para auditoria.

## 📁 Acesso ao projeto

Você pode [acessar o código fonte do projeto inicial](https://github.com/Vins-bzrra/HubStock) ou [baixá-lo](https://github.com/vins-bzrra/HubStock/archive/refs/heads/main.zip).

## 🛠️ Abrir e rodar o projeto

Após importar o projeto, você pode utilizar o Spring Tools Suite - STS. Para isso, clique em:

- **file**
- **Open Projects from File System**
- Clicar com o botão direito na pasta do projeto
- **Run As**
- **Spring Boot App**

Lembrando que o projeto usa por padrão o banco de dados PostgreSQL, antes de rodar o projeto, certifique-se que o banco de dados está instalado e configurado.
Também é necessário a inserção direta no banco de dados de pelo menos um usuário, pois apenas a rota `/login` está liberada para acesso. Todos as outras rotas se faz necessário o uso do token JWT gerado pela aplicação.

 ### Configuração

 Você pode configurar a conexão com qualquer banco de dados de sua preferência no arquivo `application.properties`. 
 Certifique-se de ter o banco de dados escolhido instalado e configurado corretamente.

 ### Build

 Se você pretende executar o projeto em um servidor dedicado, você precisará executar o seguinte comando na pasta raíz do projeto:

 ```shell
 mvn clean package
 ```

 Com esse comando será criado o arquivo `.war` do projeto para realizar o deploy no servidor. Lembrando que é necessário ter o maven instalado e configurado para executar o comando acima.
 
 Mas também é possível gerar o arquivo `.war` utilizando o STS. Para isso, você deve:
 - Clicar com o botão direito no projeto
 - **Run As**
 - **Maven build...**
 - No campo **Goals** adicione `clean package`

## ✔️ Técnicas e tecnologias utilizadas

- ``Java 17``
- ``Spring Boot Framework``
- ``PostgreSQL``
- ``Spring Tools Suite 4- STS``
- ``Paradigma de Orientação a Objetos``

## Licença

Este projeto é distribuído sob a licença **Apache License 2.0**. Consulte o arquivo [License](LICENSE.md) para obter mais informações.

## Contato

- [Email](vbzrra12@gmail.com)
- [Linkedin](www.linkedin.com/in/vinícius-da-silva-bezerra-a8596617b)
