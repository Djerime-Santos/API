# Order Manager API

API RESTful desenvolvida em **Java 8** com **Spring Boot (2.3.12.RELEASE)** e **PostgreSQL**, projetada para gerir o ciclo de vida de encomendas, stock, concorrência segura, reconciliação automática de pedidos pendentes e auditoria operacional.

## Tecnologias Utilizadas
- **Java 8**
- **Spring Boot 2.3.12.RELEASE**
- **Spring Data JPA / Hibernate** (incluindo Bloqueio Otimista / `@Version`)
- **PostgreSQL**
- **SLF4J / Logback** (para auditoria e logs)
- **Maven** (gestão de dependências)

## Arquitetura do Projeto
O projeto segue o padrão arquitetural em camadas (**Layered Architecture**):
- **`model`**: Entidades da base de dados (`User`, `Item`, `Order`, `StockMovement`, `StockAllocation`).
- **`repository`**: Interfaces de acesso a dados (`JpaRepository`).
- **`service`**: Regra de negócio (`OrderService`, `ItemService`, `UserService`, `StockMovementService`, `AuditService`), incluindo lógica de stock, FIFO e notificações.
- **`dto`**: Objetos de transferência de dados de entrada e saída (`OrderRequestDTO`, `StockRequestDTO`, etc.).
- **`controller`**: Endpoints REST expostos ao cliente (`UserController`, `ItemController`, `OrderController`, `StockMovementController`, `AuditController`).
- **`exceptions`**: Tratamento centralizado de erros (`GlobalExceptionHandler`, `ApiError`, `ResourceNotFoundException`).

## Regras de Negócio e Funcionalidades

1. **CRUD Completo & Soft Delete:**
   - Suporte para operações de Criação, Leitura, Atualização e Eliminação (*Create, Read, Update, Delete*) para todas as entidades principais.
   - Gestão de eliminação lógica (`active: boolean`), onde novos itens nascem ativos por predefinição (`active = true`).
2. **Reconciliação Automática de Stock (FIFO):**
   - Quando uma encomenda é criada e existe stock suficiente, é completada de imediato (`COMPLETED`). Caso contrário, fica pendente (`PENDING`).
   - Quando novo stock é adicionado através de um movimento `IN`, o sistema consome automaticamente as encomendas pendentes por ordem cronológica.
3. **Gestão de Concorrência Segura (Optimistic Locking):**
   - Utilização da anotação JPA `@Version` na entidade `Item` para evitar condições de corrida (*race conditions*) em atualizações simultâneas de stock.
   - Mapeamento automático de colisões de versão para respostas HTTP `409 Conflict`.
4. **Tratamento Global de Exceções (`@RestControllerAdvice`):**
   - Respostas padronizadas (`ApiError`) para toda a aplicação:
      - `400 Bad Request`: Erros de validação e regras de negócio (`IllegalArgumentException`, `IllegalStateException`).
      - `404 Not Found`: Recursos inexistentes (`ResourceNotFoundException`).
      - `409 Conflict`: Conflitos de concorrência otimista (`ObjectOptimisticLockingFailureException`).
      - `500 Internal Server Error`: Erros genéricos e inesperados do sistema.
5. **Rastreio Bidirecional & Auditoria:**
   - Relação avançada entre `Order` e `StockMovement`, permitindo rastrear exatamente quais os movimentos utilizados para satisfazer uma encomenda.
   - Trilhos de auditoria dedicados para consulta do histórico de inventário por item.
6. **Notificações por Email & Logs:**
   - Envio automático de emails de notificação ao utilizador assim que a encomenda transita para o estado de concluída.
   - Registo detalhado via SLF4J de ordens, adições de stock, emails e exceções.

---

## Como Executar o Projeto

### Pré-requisitos
- Java 8 instalado.
- Maven instalado.
- Servidor PostgreSQL a correr localmente.

### Configurar a Base de Dados

Crie uma base de dados vazia no PostgreSQL com o nome `order_manager`:
```sql
CREATE DATABASE order_manager;