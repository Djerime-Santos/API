# Order Manager API

API RESTful desenvolvida em **Java 8** com **Spring Boot (2.3.12.RELEASE)** e **PostgreSQL**, projetada para gerir o ciclo de vida de encomendas, stock e reconciliação automática de pedidos pendentes.

## Tecnologias Utilizadas
* **Java 8**
* **Spring Boot 2.3.12.RELEASE**
* **Spring Data JPA / Hibernate**
* **PostgreSQL**
* **SLF4J / Logback** (para auditoria e logs)
* **Maven** (gestão de dependências)

## Arquitetura do Projeto
O projeto segue o padrão arquitetural em camadas (**Layered Architecture**):
* **`model`**: Entidades da base de dados (`User`, `Item`, `Order`, `StockMovement`).
* **`repository`**: Interfaces de acesso a dados (`JpaRepository`).
* **`service`**: Regra de negócio (`OrderService`, `ItemService`, `UserService`, `StockMovementService`), incluindo lógica de stock e notificações.
* **`dto`**: Objetos de transferência de dados de entrada (`OrderRequestDTO`, `StockRequestDTO`).
* **`controller`**: Endpoints REST expostos ao cliente (`UserController`, `ItemController`, `OrderController`, `StockMovementController`).

## Regras de Negócio e Funcionalidades

1. **CRUD Completo:** Suporte total para operações de Criação, Leitura, Atualização e Eliminação (*Create, Read, Update, Delete*) para todas as entidades principais.
2. **Reconciliação Automática de Stock:**
    - Quando uma encomenda é criada e existe stock suficiente, é completada de imediato (`COMPLETED`). Caso contrário, fica pendente (`PENDING`).
    - Quando novo stock é adicionado através de um movimento, o sistema tenta automaticamente satisfazer as encomendas pendentes para esse item.
3. **Rastreio Bidirecional:** Relação relacional avançada entre `Order` e `StockMovement`, permitindo rastrear exatamente quais os movimentos de stock utilizados para satisfazer uma encomenda, e vice-versa.
4. **Notificações por Email:** Envio automático de emails de notificação ao utilizador assim que a sua encomenda transita para o estado de concluída.
5. **Sistema de Logs:** Registo detalhado via SLF4J de ordens concluídas, adições de stock, tentativas de envio de emails e exceções do sistema.


## Como Executar o Projeto

### Pré-requisitos
* Java 8 instalado.
* Maven instalado.
* Servidor PostgreSQL a correr localmente.

### 1. Configurar a Base de Dados
Cria uma base de dados vazia no PostgreSQL com o nome `order_manager`:
```sql
CREATE DATABASE order_manager;