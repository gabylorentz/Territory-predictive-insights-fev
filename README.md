# Territory Predictive Insights

**Um sistema que avisa antes de o córrego transbordar.**

Todo mundo que mora perto de um córrego sabe como é: chove forte, a água sobe rápido e, quando o alagamento chega, já é tarde para avisar os vizinhos. A gente quis fazer o caminho contrário. O sistema acompanha a chuva, o nível do córrego e a umidade do solo, olha para o que aconteceu nas últimas leituras e avisa quando a situação está caminhando para um problema, para alguém agir **antes**.

É um projeto em Java, de console, feito para a Global Solution de POO da FIAP (2º/2026), no tema **CitizenTech**: tecnologia a serviço de necessidades reais da cidade.

---

## Para quem é

- **Quem sofre o problema:** moradores da margem do córrego, na Zona Sul. No projeto usamos ~1.200 pessoas expostas, um número estimado e fictício, só para dar escala ao cenário.
- **Quem usa o sistema:** a associação de bairro ou a Defesa Civil local.
- **Que decisão ele apoia:** avisar os moradores e abrir o ponto de apoio.

Um cuidado que tivemos desde o começo: **o sistema não guarda dado pessoal nenhum**. O território é descrito por região e número aproximado de pessoas, nunca por nomes.

---

## Como ele funciona

O caminho de cada leitura é sempre o mesmo:

> sensor lê → o valor é validado → vai para o banco → o sistema prevê o próximo valor → compara com o limite → alerta, se for o caso

Temos três sensores (todos simulados com `Random`): **chuva** (mm/h), **nível do córrego** (cm) e **umidade do solo** (%). Para prever, usamos duas estratégias que "pensam" de formas diferentes:

- **Média móvel:** olha o nível das últimas 5 leituras.
- **Comparação com o período anterior:** compara as 3 leituras mais recentes com as 3 anteriores para ver se a situação está **subindo ou caindo**, e projeta essa tendência.

Quando a previsão chega perto ou passa do limite, o sistema emite um alerta em três níveis:

| Nível | A partir de | Na prática |
|---|---|---|
| ATENÇÃO | 80% do limite | ainda abaixo, mas se aproximando |
| ALERTA | 100% do limite | o limite foi atingido |
| CRÍTICO | 150% do limite | muito acima do esperado |

Os limites padrão são 30 mm/h de chuva, 200 cm de nível do córrego e 85% de umidade, e podem ser alterados pelo menu.

Todo alerta explica o motivo, por exemplo: `[ALERTA] Chuva: previsto 36.4 mm/h, limiar de 30.0 mm/h (121% do limite)`. Ele diz qual sensor, o que foi previsto, qual era o limite e quanto passou.

### Organização do código

| Pacote | O que mora ali |
|---|---|
| `dominio` | O "mundo" do sistema: `Medicao`, `Sensor` e suas três implementações, `ComponenteTerritorial` e `AreaDeRisco`, `DadosTerritorio`, `EstrategiaPrevisao` com as duas previsões, `Alerta` e `NivelAlerta` |
| `excecao` | As duas exceções do projeto: `DadosInvalidosException` e `FonteIndisponivelException` |
| `persistencia` | Tudo que conversa com o banco: `ConexaoBanco`, `MedicaoDAO` e `AlertaDAO` |
| `servico` | A parte que junta tudo: `ConfiguracaoSistema`, `FabricaPrevisao`, `ServicoMonitoramento` e o `MenuConsole` |

---

## Como rodar

Você precisa de **Java 17 ou superior** e do **Maven**. Na raiz do projeto:

```bash
mvn clean test           # compila e roda os 24 testes
mvn compile exec:java    # abre o menu
```

### Sobre o banco de dados

Não precisa instalar nem configurar nada. Usamos o **H2 em modo arquivo** (`jdbc:h2:./dados/monitoramento`): as tabelas `medicao` e `alerta` são criadas sozinhas na primeira execução e a pasta `dados/` aparece sozinha. Como os dados ficam em arquivo, **eles continuam lá quando o programa é fechado e aberto de novo**.

Um aviso importante para quem vai testar: na primeira vez o histórico está vazio, então as previsões ficam em silêncio (vamos explicar o porquê na reflexão crítica). **Use a opção 1 do menu pelo menos 6 vezes** para juntar leituras: a média móvel precisa de 5 e a comparação precisa de 6. Lembre que a leitura é sorteada, então às vezes um sensor falha ou devolve um valor inválido de propósito, e isso é esperado.

### IMPORTANTE:
Se os acentos aparecerem como `?` no terminal do Windows, rode `chcp 65001` antes. É só exibição, o sistema funciona igual.

### O menu

```
1) Verificar sensores
2) Rodar previsões e alertas
3) Ver histórico
4) Gerenciar dados do território
5) Status completo
0) Sair
```

Se alguém digitar uma letra, deixar em branco ou escolher uma opção que não existe, o menu avisa e volta, sem travar.

---

## Testes

São **24 testes** com JUnit 5, escritos no padrão Arrange, Act, Assert. Alguns pontos:

- Usamos `assertThrows` para provar que dados ruins são recusados (medição negativa, NaN, limiar inválido, histórico curto demais).
- O `ServicoMonitoramentoTest` usa **Mockito**: em vez de usar o banco de verdade, o teste entrega "dublês" dos DAOs. Assim dá para testar só a lógica do alerta, sem depender de banco nem de digitação.
- Os valores esperados foram calculados **na mão**, e não copiados do que o programa devolve. O código foi quebrado de propósito, removendo o Math.max, e o teste comparacaoNaoDevePreverValorNegativo ficou vermelho. Ao desfazer, voltou ao verde. Isso prova que o teste detecta o erro.

---

## Decisões que tomamos (e por quê)

**JDBC em vez de Spring/JPA.** Preferimos o caminho em que o SQL fica à vista, porque assim conseguimos explicar o que acontece em cada linha. Todo SQL usa `PreparedStatement` com `?`, nunca texto concatenado, e as conexões são fechadas com try-with-resources.

**H2 em arquivo, e não em memória.** Prever sem histórico é chute, e o histórico precisa sobreviver ao reinício. Em memória, tudo sumiria ao fechar o programa.

**Exceções que o compilador obriga a tratar.** Fizemos as duas exceções herdarem de `Exception`, e não de `RuntimeException`. Assim ninguém consegue esquecer de tratar o erro, e o menu sempre responde com uma mensagem clara.

**Objetos que nascem válidos.** A validação fica no construtor: se uma `Medicao` ou um `Alerta` existe, é porque os dados estavam certos. A `Medicao` nem tem *setters*, porque é o registro de algo que já aconteceu (às 14h30 choveu 42,5 mm/h, e isso não muda).

**Previsões com `super`.** `EstrategiaPrevisao` é abstrata e já confere se há leituras suficientes. As duas filhas sobrescrevem `prever()` e chamam `super.prever()` para reaproveitar essa conferência, depois fazem o próprio cálculo.

**Serviço que recebe os DAOs de fora.** O `ServicoMonitoramento` recebe os DAOs pelo construtor em vez de criá-los por dentro. É o que permite trocá-los por dublês nos testes.

### Os padrões criacionais

**Singleton: `ConfiguracaoSistema`.** Os limites precisam ser **os mesmos em todo o sistema**. Se alguém altera o limite da chuva pelo menu, o serviço de alertas precisa enxergar o valor novo, e não uma cópia antiga. Por isso a configuração tem uma única instância, com construtor privado e acesso por `getInstancia()`. Não aplicamos o padrão nos DAOs nem nos sensores de propósito: eles não guardam estado compartilhado, e usar Singleton ali seria só complicar.

**Factory: `FabricaPrevisao`.** Criar as estratégias de previsão exige ler a janela e o período da configuração, e não queríamos esse detalhe espalhado pelo código. A fábrica guarda uma "receita" para cada estratégia em um `Map`, então o código pede pelo nome e nunca precisa de `if` nem de `instanceof` para escolher. Para criar uma terceira estratégia, é só registrar mais uma receita.

---

## Reflexão crítica

**Quando o sistema deve ficar em silêncio?** Quando não tem leituras suficientes. Com 2 ou 3 medições, qualquer previsão seria chute, e um alerta baseado em chute pode ser pior que nenhum alerta: assusta quem não precisava ou, pior, tranquiliza quem precisava de cuidado. Por isso as estratégias exigem um mínimo de leituras (5 para a média móvel, 6 para a comparação). Abaixo disso, o sistema avisa que não dá para prever, mostra `[SILÊNCIO]` e não grava alerta nenhum. Um teste automatizado garante essa regra.

**O que os dados não representam?** Os valores aqui são simulados, e mesmo num cenário real um sensor por grandeza não conta a história de um território inteiro: a chuva em um ponto não é a chuva da bacia toda. Também vimos limites do próprio cálculo. A previsão só prolonga a tendência, sem conhecer os limites físicos do sensor: em um teste, a umidade do solo foi prevista em 100,9%, um valor impossível. E vimos a mesma chuva render NORMAL na média móvel e ALERTA na comparação, porque uma olha o nível e a outra olha a direção. Nenhuma das duas está "errada", e é justamente por isso que o sistema mostra as duas.

**Por que o alerta apoia a decisão, em vez de decidir?** Porque o sistema não conhece a rua, o morador que não pode sair de casa ou o que a equipe local já sabe. Quem decide abrir o abrigo ou bater de porta em porta é uma pessoa. Por isso o alerta nunca diz "faça isso": ele mostra o sensor, o valor previsto, o limite e o percentual, para a pessoa ver o motivo e decidir se concorda. A máquina ajuda a enxergar mais cedo, e a decisão continua sendo humana.

---

## Limitações que a gente conhece

- Os limites e a população alterados pelo menu ficam só na memória e voltam ao padrão quando o programa reinicia. As intruções pede persistência apenas para medições e alertas, mas seguimos já para um próximo passo.
- Cada vez que a opção 2 roda, ela grava um alerta novo, mesmo que a situação seja a mesma da execução anterior. O histórico pode ter alertas parecidos em sequência.
- O status da área de risco ainda não é atualizado automaticamente pelos alertas.

---

## Uso de IA

Usamos o **Microsoft Copilot** como tutor ao longo do projeto: para entender conceitos que eram novos para a gente, para interpretar mensagens de erro e para ver exemplos de estrutura antes de escrever o nosso código. Para não aceitar nada no automático, revisamos tudo na prática: rodamos o programa, calculamos na mão os valores esperados dos testes, quebramos o código de propósito para ver os testes falharem e conferimos cada parte com o enunciado.
---

## Divisão de tarefas

Dividimos o projeto em duas partes: a base do sistema e a parte de alertas, menu e testes.

### Gabrielly Lorentz (@gabylorentz): Etapas 1 a 5, a base do sistema
- Estrutura do projeto Maven (`pom.xml`, pacotes e `.gitignore`).
- Exceções customizadas: `DadosInvalidosException` e `FonteIndisponivelException`.
- `Medicao`, com validação no construtor.
- Interface `Sensor` e os três sensores simulados (chuva, nível do córrego e umidade do solo).
- `ComponenteTerritorial` (classe abstrata), `AreaDeRisco` e `DadosTerritorio`, com os limiares.
- Persistência com JDBC e H2: `ConexaoBanco` e `MedicaoDAO`.
- `EstrategiaPrevisao`, `PrevisaoMediaMovel` e `PrevisaoComparacao`.

### Maria Eduarda Luis (@marieduarda1908): Etapas 6 a 10, serviços, alertas, menu e testes
- Padrões criacionais: `ConfiguracaoSistema` (Singleton) e `FabricaPrevisao` (Factory).
- Alertas: `NivelAlerta`, `Alerta`, `AlertaDAO` e `ServicoMonitoramento`.
- `MenuConsole`, o menu interativo com `Scanner`.
- Os 24 testes: JUnit no padrão AAA, `assertThrows` e Mockito.
- README e configuração para rodar com `mvn compile exec:java`.

