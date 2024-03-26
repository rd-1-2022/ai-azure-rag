# Spring AI Retrieval Augmented Generation with Azure OpenAI

## Introduction

Retrieval Augmented Generation (RAG) is a technique that integrates your data into the AI model's responses.

First, you need to upload the documents you wish to have analyzed in an AI respoinse into a Vector Database.
This involves breaking down the documents into smaller segments because AI models typically only manage to process a few tens of kilobytes of custom data for generating responses.
After splitting, these document segments are stored in the Vector Database.

The second step involves including data from the Vector Database that is pertinent to your query when you make a request to the AI model.
This is achieved by performing a similarity search within the Vector Database to identify relevant content.

In the third step, you merge the text of your request with the documents retrieved from the Vector Database before sending it to the AI model.
This process is informally referred to as 'stuffing the prompt'.

This project demonstrates Retrieval Augmented Generation in practice and can serve as the foundation for customizing to meet your specific requirements in your own project.

## Endpoints

This project contains a web service with the following endpoints under http://localhost:8080

* POST `/data/load`
* GET `/data/count`
* POST `/data/delete`
* GET `/qa`

The `/qa` endpoint takes a `question` parameter which is the question you want to ask the AI model.
The `/qa` endpoint also takes a `stuffit` boolean parameter, whose default it true, that will 'stuff the prompt' with
similar documents to the question.  When stuffing the prompt, this follows the RAG pattern.

## Prerequisites

### Azure OpenAI Credentials

Obtain your Azure OpenAI `endpoint` and `api-key` from the Azure OpenAI Service section on [Azure Portal](https://portal.azure.com)

The Spring AI project defines a configuration property named `spring.ai.azure.openai.api-key` that you should set to the value of the `API Key` obtained from Azure

Exporting an environment variables is one way to set these configuration properties.
```shell
export SPRING_AI_AZURE_OPENAI_API_KEY=<INSERT KEY HERE>
export SPRING_AI_AZURE_OPENAI_ENDPOINT=<INSERT ENDPOINT URL HERE>
export SPRING_AI_AZURE_OPENAI_CHAT_OPTIONS_DEPLOYMENT_NAME=<INSERT NAME HERE>
```
Note, the `/resources/application.yml` references the environment variable `${SPRING_AI_AZURE_OPENAI_API_KEY}`.

## VectorStore

To run the PgVectorStore locally, using docker-compose.
From the top project directory and run:

```
docker-compose up
```

Later starts Postgres DB on localhost and port 5432.

Then you can connect to the database (password: `postgres`) and inspect or alter the `vector_store` table content:

```
psql -U postgres -h localhost -p 5432

\l
\c vector_store
\dt

select count(*) from vector_store;

delete from vector_store;
```

You can connect to the pgAdmin on http://localhost:5050  as user: `pgadmin4@pgadmin.org` and pass: `admin`.
Then navigate to the `Databases/vector_store/Schemas/public/Tables/vector_store`.

The UI tool [DBeaver](https://dbeaver.io/download/) is also a useful GUI for postgres.

## Building and running

```
./mvnw spring-boot:run
```

## Access the endpoints

The first thing you should do is load the data.  The examples show usage with the [HTTPie](https://httpie.io/) command line utility as it simplifies sending HTTP requests with data as compared to curl.

### Loading, counting and deleting data

```shell
http POST http://localhost:8080/data/load
```

Next you can see how many document fragments were loaded into the Vector Store using

```shell
http http://localhost:8080/data/count
```
If you want to start over, for example because you changed in the code which document is being loaded, then execute

```shell
http POST http://localhost:8080/data/delete
```

### Q&A over the document using QueryEngine

Send your question to the QueryEngine using

```shell
http --body --unsorted localhost:8080/qa/engine question=="What is the purpose of Carina?"

```

The response is

```json
{
    "question": "What is the purpose of Carina?",
    "answer": "The purpose of Carina is to provide a safe, easy-to-use, online location-based care matching service. It serves individuals and families searching for home care or child care, as well as care professionals looking for good jobs. Carina is committed to building community and prioritizing people over profit."
}

```

### Q&A over the document

Send you question to the AI Model using

```shell
http --body --unsorted localhost:8080/qa message==<insert question here>
```

Note that there are two equal signs `==` , that separate the key-value pairs

To ask the same question but without the similar documents stuffing the prompt, that is, not using the RAG pattern,

```shell
http --body --unsorted http://localhost:8080/qa message==<insert question here> stuffit==false
```

#### Examples

```shell
$ http --body --unsorted localhost:8080/qa question=="What is the purpose of Carina?"
{
    "question": "What is the purpose of Carina?",
    "answer": "The purpose of Carina is to provide a safe and easy-to-use online care matching service. It aims to connect care providers with individuals and families who are in need of home care or child care services. Carina prioritizes building community and supporting care workers by bringing good jobs to them. Its goal is to strengthen the care economy and support workers, individuals, and families in the process."
}
```

and without stuffing the prompt

```shell
$ http --body --unsorted localhost:8080/qa question=="What is the purpose of Carina?" stuffit==false
{
    "question": "What is the purpose of Carina?",
    "answer": "Carina is a constellation located in the southern sky. It does not have a specific purpose, but like other constellations, it serves as a way to organize and identify stars in the night sky. Constellations have been used for navigation, storytelling, and scientific observation throughout history."
}

```
