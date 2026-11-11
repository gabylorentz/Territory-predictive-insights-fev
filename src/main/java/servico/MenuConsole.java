package servico;

import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

public class MenuConsole {

    private final Scanner scanner = new Scanner(System.in);
    private final List<Sensor> sensores;
    private final MedicaoDAO medicaoDAO;
    private final AlertaDAO alertaDAO;
    private final ServicoMonitoramento servico;
    private final FabricaPrevisao fabrica;
    private final DadosTerritorio dadosTerritorio;
    private final AreaDeRisco area;

    public MenuConsole(List<Sensor> sensores, MedicaoDAO medicaoDAO, AlertaDAO alertaDAO,
                       ServicoMonitoramento servico, FabricaPrevisao fabrica,
                       DadosTerritorio dadosTerritorio, AreaDeRisco area) {
        this.sensores = sensores;
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.servico = servico;
        this.fabrica = fabrica;
        this.dadosTerritorio = dadosTerritorio;
        this.area = area;
    }

    public void iniciar() {
        boolean executando = true;
        while (executando) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opção: ");
            switch (opcao) {
                case 1 -> verificarSensores();
                case 2 -> rodarPrevisoes();
                case 3 -> verHistorico();
                case 4 -> gerenciarTerritorio();
                case 5 -> statusCompleto();
                case 0 -> executando = false;
                default -> System.out.println("[AVISO] Opção inexistente. Use um número do menu.");
            }
        }
        System.out.println("Sistema encerrado.");
        scanner.close();
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("===== TERRITORY PREDICTIVE INSIGHTS =====");
        System.out.println("1) Verificar sensores");
        System.out.println("2) Rodar previsões e alertas");
        System.out.println("3) Ver histórico");
        System.out.println("4) Gerenciar dados do território");
        System.out.println("5) Status completo");
        System.out.println("0) Sair");
    }


    private int lerInteiro(String texto) {
        System.out.print(texto);
        String entrada = scanner.nextLine().trim();
        try {
            return Integer.parseInt(entrada);
        } catch (NumberFormatException e) {
            System.out.println("[AVISO] Digite apenas números. Você digitou: \"" + entrada + "\"");
            return -1;
        }
    }

    private double lerDecimal(String texto) throws DadosInvalidosException {
        System.out.print(texto);
        String entrada = scanner.nextLine().trim().replace(',', '.');
        try {
            return Double.parseDouble(entrada);
        } catch (NumberFormatException e) {
            throw new DadosInvalidosException("Número inválido: \"" + entrada + "\"");
        }
    }

    private Optional<Sensor> escolherSensor() {
        for (int i = 0; i < sensores.size(); i++) {
            System.out.println((i + 1) + ") " + sensores.get(i).getTipo());
        }
        int escolha = lerInteiro("Sensor: ");
        if (escolha < 1 || escolha > sensores.size()) {
            System.out.println("[AVISO] Sensor inexistente.");
            return Optional.empty();
        }
        return Optional.of(sensores.get(escolha - 1));
    }

    private void verificarSensores() {
        System.out.println("--- Leitura dos sensores ---");
        for (Sensor sensor : sensores) {
            try {
                Medicao medicao = sensor.ler();
                medicaoDAO.salvar(medicao);
                System.out.println("[OK] Gravada: " + medicao);
            } catch (DadosInvalidosException e) {
                System.out.println("[AVISO] Dado inválido: " + e.getMessage());
            } catch (FonteIndisponivelException e) {
                System.out.println("[FALHA] Sensor indisponível: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("[FALHA] Erro ao gravar no banco: " + e.getMessage());
            }
        }
    }

    private void rodarPrevisoes() {
        System.out.println("--- Previsões e alertas ---");
        List<EstrategiaPrevisao> estrategias = fabrica.criarTodas();

        for (Sensor sensor : sensores) {
            System.out.println(">> " + sensor.getTipo());

            List<Medicao> historico;
            try {
                historico = medicaoDAO.listarPorSensor(sensor.getTipo());
            } catch (SQLException | DadosInvalidosException e) {
                System.out.println("[FALHA] Erro ao ler histórico: " + e.getMessage());
                continue;
            }

            for (EstrategiaPrevisao estrategia : estrategias) {
                try {
                    double previsto = estrategia.prever(historico);
                    System.out.println("[" + estrategia.getNome() + "] "
                            + estrategia.explicar(previsto, sensor.getUnidade()));

                    Optional<Alerta> alerta = servico.avaliar(
                            sensor.getTipo(), sensor.getUnidade(), estrategia);
                    if (alerta.isPresent()) {
                        area.setStatus(alerta.get().getNivel().getRotulo());
                        System.out.println("   " + alerta.get().getMensagem());
                    } else {
                        System.out.println("   [NORMAL] Abaixo de 80% do limiar.");
                    }
                } catch (DadosInvalidosException e) {
                    System.out.println("[SILÊNCIO] " + e.getMessage());
                } catch (SQLException e) {
                    System.out.println("[FALHA] Erro no banco: " + e.getMessage());
                }
            }
        }
    }

    private void verHistorico() {
        System.out.println("1) Medições de um sensor");
        System.out.println("2) Alertas emitidos");
        int opcao = lerInteiro("Opção: ");

        try {
            switch (opcao) {
                case 1 -> {
                    Optional<Sensor> sensor = escolherSensor();
                    if (sensor.isPresent()) {
                        List<Medicao> lista = medicaoDAO.listarPorSensor(sensor.get().getTipo());
                        if (lista.isEmpty()) {
                            System.out.println("Nenhum registro encontrado.");
                        }
                        for (Medicao m : lista) {
                            System.out.println(m);
                        }
                    }
                }
                case 2 -> {
                    List<Alerta> alertas = alertaDAO.listarTodos();
                    if (alertas.isEmpty()) {
                        System.out.println("Nenhum registro encontrado.");
                    }
                    for (Alerta a : alertas) {
                        System.out.println(a.getMensagem());
                    }
                }
                default -> System.out.println("[AVISO] Opção inexistente.");
            }
        } catch (SQLException | DadosInvalidosException e) {
            System.out.println("[FALHA] Erro ao consultar o histórico: " + e.getMessage());
        }
    }

    private void gerenciarTerritorio() {
        System.out.println("1) Ver limiares");
        System.out.println("2) Alterar limiar");
        System.out.println("3) Ver área de risco");
        System.out.println("4) Alterar população exposta");
        int opcao = lerInteiro("Opção: ");

        try {
            switch (opcao) {
                case -1 -> {
                    for (Sensor sensor : sensores) {
                        System.out.println(sensor.getTipo() + ": "
                                + dadosTerritorio.getLimiar(sensor.getTipo())
                                + " " + sensor.getUnidade());
                    }
                }
                case 2 -> {
                    Optional<Sensor> sensor = escolherSensor();
                    if (sensor.isPresent()) {
                        double novo = lerDecimal("Novo limiar: ");
                        dadosTerritorio.setLimiar(sensor.get().getTipo(), novo);
                        System.out.println("[OK] Limiar de " + sensor.get().getTipo()
                                + " alterado para " + novo + " " + sensor.get().getUnidade());
                    }
                }
                case 3 -> System.out.println(area.verificarStatus());
                case 4 -> {
                    int novaPopulacao = lerInteiro("Nova população exposta: ");
                    area.setPopulacaoExposta(novaPopulacao);
                    System.out.println("[OK] " + area.verificarStatus());
                }
                default -> System.out.println("[AVISO] Opção inexistente.");
            }
        } catch (DadosInvalidosException e) {
            System.out.println("[AVISO] " + e.getMessage());
        }
    }

    private void statusCompleto() {
        System.out.println("===== STATUS COMPLETO =====");
        System.out.println(area.verificarStatus());

        try {
            for (Sensor sensor : sensores) {
                int leituras = medicaoDAO.listarPorSensor(sensor.getTipo()).size();
                System.out.println(sensor.getTipo()
                        + " | funcionando: " + (sensor.estaFuncionando() ? "sim" : "não")
                        + " | limiar: " + dadosTerritorio.getLimiar(sensor.getTipo())
                        + " " + sensor.getUnidade()
                        + " | leituras: " + leituras);
            }
            System.out.println("Alertas emitidos: " + alertaDAO.listarTodos().size());
        } catch (SQLException | DadosInvalidosException e) {
            System.out.println("[FALHA] Erro ao montar o status: " + e.getMessage());
        }
    }
}