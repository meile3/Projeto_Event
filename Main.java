import model.User;
import model.Event;
import model.EventCategory;
import service.EventManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static EventManager manager = new EventManager();
    private static User currentUser = null;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void main(String[] args) {
        System.out.println("=== Sistema de Eventos - Console ===");
        boolean running = true;
        while (running) {
            showMainMenu();
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1": cadastrarUsuario(); break;
                case "2": cadastrarEvento(); break;
                case "3": listarEventos(); break;
                case "4": participarEvento(); break;
                case "5": verMeusEventos(); break;
                case "6": cancelarParticipacao(); break;
                case "7": manager.saveToFile(); System.out.println("Eventos salvos."); break;
                case "0": running = false; break;
                default: System.out.println("Opção inválida.");
            }
        }
        System.out.println("Saindo... Até logo.");
    }

    private static void showMainMenu() {
        System.out.println();
        System.out.println("Usuario atual: " + (currentUser == null ? "(nenhum cadastrado)" : currentUser.getNome() + " - " + currentUser.getEmail()));
        System.out.println("1) Cadastrar/Selecionar usuário");
        System.out.println("2) Cadastrar evento");
        System.out.println("3) Listar eventos (ordenados e estado)");
        System.out.println("4) Participar de evento");
        System.out.println("5) Ver meus eventos confirmados");
        System.out.println("6) Cancelar participação");
        System.out.println("7) Salvar eventos agora");
        System.out.println("0) Sair");
        System.out.print("Escolha: ");
    }

    private static void cadastrarUsuario() {
        System.out.print("Nome: "); String nome = scanner.nextLine().trim();
        System.out.print("Email: "); String email = scanner.nextLine().trim();
        System.out.print("Telefone: "); String tel = scanner.nextLine().trim();
        System.out.print("Cidade: "); String city = scanner.nextLine().trim();
        currentUser = new User(nome, email, tel, city);
        System.out.println("Usuário cadastrado: " + currentUser);
    }

    private static void cadastrarEvento() {
        System.out.print("Nome do evento: "); String nome = scanner.nextLine().trim();
        System.out.print("Endereço: "); String endereco = scanner.nextLine().trim();
        System.out.println("Categorias: " + java.util.Arrays.toString(EventCategory.values()));
        System.out.print("Escolha categoria (ex: FESTA): "); String catS = scanner.nextLine().trim().toUpperCase();
        EventCategory cat;
        try { cat = EventCategory.valueOf(catS); } catch (Exception ex) { cat = EventCategory.CULTURAL; }
        System.out.print("Horário (formato ISO ex: 2025-10-20T20:00): "); String time = scanner.nextLine().trim();
        LocalDateTime horario;
        try { horario = LocalDateTime.parse(time, FORMATTER); } catch (Exception ex) {
            System.out.println("Formato inválido. Usando agora + 1 hora.");
            horario = LocalDateTime.now().plusHours(1);
        }
        System.out.print("Descrição: "); String desc = scanner.nextLine().trim();
        int id = manager.getNextId();
        Event e = new Event(id, nome, endereco, cat, horario, desc);
        manager.addEvent(e);
        System.out.println("Evento criado: " + e);
    }

    private static void listarEventos() {
        List<Event> all = manager.getAllSortedByTime();
        System.out.println("\n== Eventos cadastrados ==");
        for (Event e : all) {
            String status = e.isHappeningNow() ? "(OCORRENDO)" : (e.isPast() ? "(JÁ OCORREU)" : "(AGENDADO)");
            System.out.printf("[%d] %s - %s %s\n", e.getId(), e.getNome(), e.getHorario().format(FORMATTER), status);
            System.out.println("   Categoria: " + e.getCategoria());
            System.out.println("   Endereço: " + e.getEndereco());
            System.out.println("   Descrição: " + e.getDescricao());
            System.out.println("   Participantes: " + e.getParticipantes().size());
        }
    }

    private static void participarEvento() {
        if (currentUser == null) { System.out.println("Cadastre um usuário antes."); return; }
        listarEventos();
        System.out.print("Digite o id do evento: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Event e = manager.getEventById(id);
            if (e == null) { System.out.println("Evento não encontrado."); return; }
            if (e.isParticipating(currentUser.getEmail())) { System.out.println("Você já participa."); return; }
            manager.joinEvent(id, currentUser.getEmail());
            System.out.println("Participação confirmada: " + e.getNome());
        } catch (Exception ex) { System.out.println("Entrada inválida."); }
    }

    private static void verMeusEventos() {
        if (currentUser == null) { System.out.println("Cadastre um usuário antes."); return; }
        List<Event> my = manager.getAllSortedByTime().stream()
                .filter(e -> e.isParticipating(currentUser.getEmail()))
                .collect(Collectors.toList());
        System.out.println("\n== Meus eventos confirmados ==");
        for (Event e : my) {
            String status = e.isHappeningNow() ? "(OCORRENDO)" : (e.isPast() ? "(JÁ OCORREU)" : "(AGENDADO)");
            System.out.printf("[%d] %s - %s %s\n", e.getId(), e.getNome(), e.getHorario().format(FORMATTER), status);
        }
    }

    private static void cancelarParticipacao() {
        if (currentUser == null) { System.out.println("Cadastre um usuário antes."); return; }
        verMeusEventos();
        System.out.print("Digite o id do evento: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Event e = manager.getEventById(id);
            if (e == null) { System.out.println("Evento não encontrado."); return; }
            if (!e.isParticipating(currentUser.getEmail())) { System.out.println("Você não participa."); return; }
            manager.leaveEvent(id, currentUser.getEmail());
            System.out.println("Participação cancelada.");
        } catch (Exception ex) { System.out.println("Entrada inválida."); }
    }
}
