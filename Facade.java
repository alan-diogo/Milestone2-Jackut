package br.ufal.ic.p2.jackut.model;

import br.ufal.ic.p2.jackut.model.exceptions.*;
import br.ufal.ic.p2.jackut.model.models.*;
import java.io.Serializable;
import java.util.Set;

/**
 * Fachada principal do sistema Jackut. Fornece uma interface simplificada para todas as opera��es do sistema,
 * encapsulando a complexidade da l�gica de neg�cio e servindo como ponto �nico de integra��o para interfaces externas.
 *
 * <p>Gerencia persist�ncia de dados automaticamente ao inicializar e encerrar o sistema.</p>
 *
 * @see Sistema
 */
public class Facade implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Inst�ncia do sistema que cont�m a l�gica de neg�cio
     */
    private Sistema sistema;

    /**
     * Inicializa a fachada carregando dados persistentes do arquivo "dados.ser".
     * Se o arquivo n�o existir, cria um novo sistema vazio.
     */
    public Facade() {
        this.sistema = Sistema.carregarDados();
    }

    /**
     * Reinicia completamente o sistema, removendo todos os usu�rios, sess�es e comunidades.
     */
    public void zerarSistema() {
        sistema.zerarSistema();
    }

    /**
     * Cria um novo usu�rio no sistema.
     * @param login Identificador �nico (case-sensitive)
     * @param senha Senha em texto plano (n�o criptografada)
     * @param nome Nome p�blico para exibi��o
     * @throws IllegalArgumentException Se login/senha forem vazios ou login j� existir
     */
    public void criarUsuario(String login, String senha, String nome) {
        sistema.criarUsuario(login, senha, nome);
    }

    /**
     * Autentica um usu�rio e inicia uma nova sess�o.
     * @param login Login do usu�rio
     * @param senha Senha correspondente
     * @return ID �nico da sess�o no formato UUID
     * @throws IllegalArgumentException Se credenciais forem inv�lidas
     */
    public String abrirSessao(String login, String senha) {
        return sistema.abrirSessao(login, senha);
    }

    /**
     * Obt�m informa��es do perfil de um usu�rio.
     * @param login Login do usu�rio consultado
     * @param atributo Nome do campo (ex: "nome", "cidade")
     * @return Valor armazenado no atributo
     * @throws UsuarioNaoCadastradoException Se o usu�rio n�o existir
     * @throws AtributoNaoPreenchidoException Se o atributo n�o estiver definido
     */
    public String getAtributoUsuario(String login, String atributo) {
        return sistema.getAtributoUsuario(login, atributo);
    }

    /**
     * Edita um atributo do perfil do usu�rio da sess�o atual.
     * @param idSessao ID da sess�o
     * @param atributo Nome do atributo
     * @param valor Novo valor do atributo
     * @throws IllegalArgumentException Se a sess�o for inv�lida
     */
    public void editarPerfil(String idSessao, String atributo, String valor) {
        sistema.editarPerfil(idSessao, atributo, valor);
    }

    /**
     * Adiciona um amigo para o usu�rio da sess�o atual.
     * @param idSessao ID da sess�o
     * @param amigo Login do amigo a ser adicionado
     * @throws UsuarioNaoCadastradoException Se o usu�rio ou amigo n�o existirem
     * @throws AmizadeExistenteException Se j� existir amizade ou convite pendente
     */
    public void adicionarAmigo(String idSessao, String amigo) {
        sistema.adicionarAmigo(idSessao, amigo);
    }

    /**
     * Verifica se dois usu�rios s�o amigos.
     * @param login Login do primeiro usu�rio
     * @param amigo Login do segundo usu�rio
     * @return true se forem amigos, false caso contr�rio
     * @throws IllegalArgumentException Se algum dos usu�rios n�o existir
     */
    public boolean ehAmigo(String login, String amigo) {
        return sistema.ehAmigo(login, amigo);
    }

    /**
     * Obt�m a lista de amigos de um usu�rio.
     * @param login Login do usu�rio
     * @return String formatada com a lista de amigos no formato {amigo1,amigo2,...}
     * @throws IllegalArgumentException Se o usu�rio n�o existir
     */
    public String getAmigos(String login) {
        return sistema.getAmigos(login);
    }

    /**
     * Envia um recado para outro usu�rio.
     * @param idSessao ID da sess�o do remetente
     * @param destinatario Login do destinat�rio
     * @param mensagem Conte�do do recado
     * @throws IllegalArgumentException Se a sess�o for inv�lida, destinat�rio n�o existir ou for o mesmo que o remetente
     */
    public void enviarRecado(String idSessao, String destinatario, String mensagem) {
        sistema.enviarRecado(idSessao, destinatario, mensagem);
    }

    /**
     * L� o pr�ximo recado da fila do usu�rio da sess�o atual.
     * @param idSessao ID da sess�o
     * @return Conte�do do recado
     * @throws IllegalArgumentException Se a sess�o for inv�lida
     * @throws IllegalStateException Se n�o houver recados
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
     * @param sessaoId ID da sess�o do usu�rio criador
     * @param nome Nome �nico da comunidade
     * @param descricao Descri��o detalhada
     * @throws ComunidadeExistenteException Se o nome j� estiver em uso
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
     * @param idSessao ID da sess�o do remetente
     * @param comunidade Nome da comunidade alvo
     * @param mensagem Conte�do da mensagem
     * @throws ComunidadeNaoExisteException Se a comunidade n�o existir
     * @throws UsuarioNaoCadastradoException Se a sess�o for inv�lida
     */
    public void enviarMensagem(String idSessao, String comunidade, String mensagem) {
        sistema.enviarMensagemComunidade(idSessao, comunidade, mensagem);
    }

    public String lerMensagem(String idSessao) {
        Sessao sessao = sistema.getSessao(idSessao);
        return sessao.getUsuario().lerMensagem(); // Chama o novo m�todo
    }
    /**
     * Gerencia relacionamentos: adiciona um �dolo.
     * @param idSessao ID da sess�o do usu�rio
     * @param idolo Login do �dolo a ser adicionado
     * @throws JaEhFaException Se j� for f� do �dolo
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