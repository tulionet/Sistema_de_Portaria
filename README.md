# Sistema de Portaria Empresarial PORTAGUARD

**Tecnologias utilizadas:** Java.

**Banco de Dados:** SQL Server.

## Recursos Principais:
- **Registro de Usuários:** O administrador do sistema poderá realizar o registro de novos usuários(porteiros) conforme necessário.

- **Registro de Visitantes:** Como qualquer outra portaria, o sistema irá solicitar os dados de entrada e o porteiro deve inserir os dados do visitante, como: Nome, CPF, Nome da Empresa, etc. Assim que os dados forem coletados e o formulário for enviado, o sistema irá coletar a data e hora que o visitante foi registrado e atribuir o ID único do responsável para o mesmo.

- **Registro de Notas Fiscais:** Como estamos nos referindo à uma portaria empresarial, o porteiro também deve registrar a entrada e saída de notas fiscais. Assim como o registro de visitantes, será coletado alguns dados sobre o "entregador" da nota, e o número da nota.
E novamente, após o envio do formulário, será coletado a data e hora na qual a nota fiscal foi recebida/enviada. Também será coletado quem foi o responsável por aquele registro.

- **Filtro por Atributo:** O Sistema contém um filtro para o banco de dados, que permitirá buscas mais precisas. O filtro permite que o usuário escolha por quais atributos a pesquisa será realizada.


## Representações visuais do sistema - Diagramas:

### Diagrama de Componentes
<img src="/resources/DiagramaComponentes.png">

### Diagrama de Sequência
<img src="/resources/DiagramaSequencia.png">

### Diagrama de Casos de Uso
<img src="/resources/DiagramaCasosDeUso.png">
