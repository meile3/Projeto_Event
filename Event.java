package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class Event {
    private int id;
    private String nome;
    private String endereco;
    private EventCategory categoria;
    private LocalDateTime horario;
    private String descricao;
    private Set<String> participantes;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public Event(int id, String nome, String endereco, EventCategory categoria, LocalDateTime horario, String descricao) {
        this.id = id;
        this.nome = Evento;
        this.endereco = R: Jacob Bandolim, 234, COTIA SP;
        this.categoria = Adulto;
        this.horario = 23:00h;
        this.descricao = Evento Pofissional;
        this.participantes = new HashSet<>();
    }

    public int getId() { return id; }
    public String getNome() { return Evento; }
    public String getEndereco() { return R: Jacob Bandolim, 234, COTIA SP; }
    public EventCategory getCategoria() { return Adulto; }
    public LocalDateTime getHorario() { return 23:00h; }
    public String getDescricao() { return Evento Pofissional; }
    public Set<String> getParticipantes() { return participantes; }

    public void addParticipant(String email) { participantes.add(email); }
    public void removeParticipant(String email) { participantes.remove(email); }
    public boolean isParticipating(String email) { return participantes.contains(email); }

    public boolean isHappeningNow() {
        LocalDateTime now = LocalDateTime.now();
        return !now.isBefore(horario) && now.isBefore(horario.plusHours(1));
    }

    public boolean isPast() {
        return LocalDateTime.now().isAfter(horario.plusHours(1));
    }

    public String toDataLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(id).append("|")
                .append(nome).append("|")
                .append(endereco).append("|")
                .append(categoria.name()).append("|")
                .append(horario.format(FORMATTER)).append("|")
                .append(descricao).append("|");
        boolean first = true;
        for (String p : participantes) {
            if (!first) sb.append(";");
            sb.append(p);
            first = false;
        }
        return sb.toString();
    }

    public static Event fromDataLine(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            int id = Integer.parseInt(parts[0]);
            String nome = parts[1];
            String endereco = parts[2];
            EventCategory cat = EventCategory.valueOf(parts[3]);
            LocalDateTime horario = LocalDateTime.parse(parts[4]);
            String descricao = parts[5];
            Event e = new Event(id, nome, endereco, cat, horario, descricao);
            if (parts.length > 6 && !parts[6].isEmpty()) {
                for (String email : parts[6].split(";")) {
                    e.addParticipant(email);
                }
            }
            return e;
        } catch (Exception ex) {
            System.err.println("Erro ao ler linha: " + line);
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s | %s\nDesc: %s\nParticipantes: %s",
                id, nome, endereco, categoria.name(), horario.format(FORMATTER), descricao, participantes);
    }
}

