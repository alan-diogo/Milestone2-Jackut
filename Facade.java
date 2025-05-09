package br.ufal.ic.p2.jackut.model;

import br.ufal.ic.p2.jackut.model.exceptions.*;
import br.ufal.ic.p2.jackut.model.models.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Fachada principal do sistema Jackut. Fornece uma interface simplificada para todas as operações do sistema,
 * encapsulando a complexidade da lógica de negócio e servindo como ponto único de integração para interfaces externas.
 *
 * <p>Gerencia persistência de dados automaticamente ao inicializar e encerrar o sistema.</p>
 *
 * @see Sistema
 */
public class Facade implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Instância do sistema que contém a lógica de negócio
     */
    private Sistema sistema;

    /**
     * Inicializa a fachada carregando dados persistentes do arquivo "dados.ser".
     * Se o arquivo não existir, cria um novo sistema vazio.
     */
    public Facade() {
        this.sistema = Sistema.carregarDados();
    }

    /**
     * Reinicia completamente o sistema, removendo todos os usuários, sessões e comunidades.
     */
    public void zerarSistema() {
        sistema.zerarSistema();
    }

    /**
     * Cria um novo usuário no sistema.
     * @param login Identificador único (case-sensitive)
     * @param senha Senha em texto plano (não criptografada)
     * @param nome Nome público para exibição
     * @throws IllegalArgumentException Se login/senha forem vazios ou login já existir
     */
    public void criarUsuario(String login, String senha, String nome) {
        sistema.criarUsuario(login, senha, nome);
    }

    /**
     * Autentica um usuário e inicia uma nova sessão.
     * @param login Login do usuário
     * @param senha Senha correspondente
     * @return ID único da sessão no formato UUID
     * @throws IllegalArgumentException Se credenciais forem inválidas
     */
    public String abrirSessao(String login, String senha) {
        return sistema.abrirSessao(login, senha);
    }

    /**
     * Obtém informações do perfil de um usuário.
     * @param login Login do usuário consultado
     * @param atributo Nome do campo (ex: "nome", "cidade")
     * @return Valor armazenado no atributo
     * @throws UsuarioNaoCadastradoException Se o usuário não existir
     * @throws AtributoNaoPreenchidoException Se o atributo não estiver definido
     */
    public String getAtributoUsuario(String login, String atributo) {
        return sistema.getAtributoUsuario(login, atributo);
    }

    /**
     * Edita um atributo do perfil do usuário da sessão atual.
     * @param idSessao ID da sessão
     * @param atributo Nome do atributo
     * @param valor Novo valor do atributo
     * @throws IllegalArgumentException Se a sessão for inválida
     */
    public void editarPerfil(String idSessao, String atributo, String valor) {
        sistema.editarPerfil(idSessao, atributo, valor);
    }

    /**
     * Adiciona um amigo para o usuário da sessão atual.
     * @param idSessao ID da sessão
     * @param amigo Login do amigo a ser adicionado
     * @throws UsuarioNaoCadastradoException Se o usuário ou amigo não existirem
     * @throws AmizadeExistenteException Se já existir amizade ou convite pendente
     */
    public void adicionarAmigo(String idSessao, String amigo) {
        sistema.adicionarAmigo(idSessao, amigo);
    }

    /**
     * Verifica se dois usuários são amigos.
     * @param login Login do primeiro usuário
     * @param amigo Login do segundo usuário
     * @return true se forem amigos, false caso contrário
     * @throws IllegalArgumentException Se algum dos usuários não existir
     */
    public boolean ehAmigo(String login, String amigo) {
        return sistema.ehAmigo(login, amigo);
    }

    /**
     * Obtém a lista de amigos de um usuário.
     * @param login Login do usuário
     * @return String formatada com a lista de amigos no formato {amigo1,amigo2,...}
     * @throws IllegalArgumentException Se o usuário não existir
     */
    public String getAmigos(String login) {
        return sistema.getAmigos(login);
    }

    /**
     * Envia um recado para outro usuário.
     * @param idSessao ID da sessão do remetente
     * @param destinatario Login do destinatário
     * @param mensagem Conteúdo do recado
     * @throws IllegalArgumentException Se a sessão for inválida, destinatário não existir ou for o mesmo que o remetente
     */
    public void enviarRecado(String idSessao, String destinatario, String mensagem) {
        sistema.enviarRecado(idSessao, destinatario, mensagem);
    }

    /**
     * Lê o próximo recado da fila do usuário da sessão atual.
     * @param idSessao ID da sessão
     * @return Conteúdo do recado
     * @throws IllegalArgumentException Se a sessão for inválida
     * @throws IllegalStateException Se não houver recados
     */
    public String lerRecado(String idSessao) {
        return sistema.lerRecado(idSessao);
    }

    /**
     * Encerra o sistema, salvando os dados persistentes.
     */
    public void encerrarSistema() {
        sistema.salvarDados();
    }
    /**
     * Gerencia comunidades: cria uma nova comunidade.
     * @param sessaoId ID da sessão do usuário criador
     * @param nome Nome único da comunidade
     * @param descricao Descrição detalhada
     * @throws ComunidadeExistenteException Se o nome já estiver em uso
     */
    public void criarComunidade(String sessaoId, String nome, String descricao) {
        Sessao sessao = sistema.getSessao(sessaoId);
        sistema.criarComunidade(nome, descricao, sessao.getUsuario().getLogin());
    }
    public void removerUsuario(String idSessao) {
        sistema.removerUsuario(idSessao);
    }


    public String getDescricaoComunidade(String nome) {
        return sistema.getDescricaoComunidade(nome);
    }
    public String getDonoComunidade(String nome) {
        return sistema.getDonoComunidade(nome); // Note o "m" em "Comunidade"
    }

    public String getMembrosComunidade(String nome) {
        return sistema.getMembrosComunidade(nome);
    }

    private String formatarMembros(Set<String> membros) {
        return "{" + String.join(",", membros) + "}";
    }

    public String getComunidades(String login) {
        return sistema.getComunidadesDoUsuarioFormatado(login);
    }

    public void adicionarComunidade(String sessaoId, String nomeComunidade) {
        sistema.adicionarMembroComunidade(
                nomeComunidade,
                sistema.getSessao(sessaoId).getUsuario().getLogin()
        );
    }
    private String formatarComunidades(Set<String> comunidades) {
        return "{" + String.join(",", comunidades) + "}";
    }

    /**
     * Envia mensagem para todos os membros de uma comunidade.
     * @param idSessao ID da sessão do remetente
     * @param comunidade Nome da comunidade alvo
     * @param mensagem Conteúdo da mensagem
     * @throws ComunidadeNaoExisteException Se a comunidade não existir
     * @throws UsuarioNaoCadastradoException Se a sessão for inválida
     */
    public void enviarMensagem(String idSessao, String comunidade, String mensagem) {
        sistema.enviarMensagemComunidade(idSessao, comunidade, mensagem);
    }

    public String lerMensagem(String idSessao) {
        Sessao sessao = sistema.getSessao(idSessao);
        return sessao.getUsuario().lerMensagem(); // Chama o novo método
    }
    /**
     * Gerencia relacionamentos: adiciona um ídolo.
     * @param idSessao ID da sessão do usuário
     * @param idolo Login do ídolo a ser adicionado
     * @throws JaEhFaException Se já for fã do ídolo
     * @throws BloqueioAutoIdolException Se tentar adicionar a si mesmo
     */
    public void adicionarIdolo(String idSessao, String idolo) {
        sistema.adicionarIdolo(idSessao, idolo);
    }

    public boolean ehFa(String login, String idolo) {
        return sistema.ehFa(login, idolo);
    }

    public String getFas(String login) {
        return sistema.getFas(login);
    }

    public void adicionarPaquera(String idSessao, String paquera) {
        sistema.adicionarPaquera(idSessao, paquera);
    }

    public boolean ehPaquera(String idSessao, String paquera) {
        Sessao sessao = sistema.getSessao(idSessao);
        return sistema.ehPaquera(sessao.getUsuario().getLogin(), paquera);
    }

    public String getPaqueras(String idSessao) {
        Sessao sessao = sistema.getSessao(idSessao);
        return sistema.getPaqueras(sessao.getUsuario().getLogin());
    }

    public void adicionarInimigo(String idSessao, String inimigo) {
        sistema.adicionarInimigo(idSessao, inimigo);
    }

    public String getInimigos(String login) {
        return sistema.getInimigos(login);
    }
}