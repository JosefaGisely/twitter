/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uff.twitter.controler;

import br.uff.twitter.model.Usuario;
import br.uff.twitter.model.UsuarioDAO;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author JP
 */
public class UsuarioServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        switch(Integer.valueOf(request.getParameter("operacao"))){
            case 0:
                criaUsuario(request, response);
                break;
            case 1:
                listaUsuarios(request,response);
                break;
            case 2:
                atualizaUsuario(request, response);
//                listaUsuarios(request, response);
                break;
            case 3:
                removeUsuario(request, response);
                listaUsuarios(request, response);
                break;
            case 4:
                buscaUmUsuario(request, response);
                break;
            case 5:
                fazLogin(request, response);
                break;
            case 6:
                fazLogout(request, response);
                break;
            case 7:
                alteraSenha(request, response);
                break;
            default:
                break;
        }
    }
    
    private void criaUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException{
        // Cria um novo usuário com os dados dos Form
        Usuario usuario = new Usuario(request.getParameter("nomeCompleto"),
//                Long.parseLong(request.getParameter("dataNascimento")),
                00000,
                request.getParameter("apelido"), 
                request.getParameter("email"), 
                request.getParameter("senha"));

        // Cria um objeto de acesso ao BD
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        // Chama método para cadastrar usuário
        usuarioDAO.adiciona(usuario);

        try {
            usuarioDAO.fechaConexao();
//            listaUsuarios(request,response);
            request.getSession().setAttribute("usuarioLogado", usuario);
            response.sendRedirect("feed.jsp");
//            request.getRequestDispatcher("/twitter/UsuarioServlet?operacao=1").forward(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void listaUsuarios(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException{
        List<Usuario> usuariosList = new ArrayList<Usuario>();
        usuariosList = (new UsuarioDAO()).listaTodos();
        
        // Adiciona a lista de usuário 
        request.setAttribute("usarios", usuariosList);
        // Troca de tela pelo Dispatcher
        getServletConfig().getServletContext().getRequestDispatcher("/listaUsuariosJSP.jsp").forward(request, response);
    }
    
    private void removeUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException{
        // Cria um objeto de acesso ao BD
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        // Chama método para cadastrar usuário
        usuarioDAO.remove(Integer.valueOf(request.getParameter("id")));

        try {
            usuarioDAO.fechaConexao();
            listaUsuarios(request,response);
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void atualizaUsuario(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException{
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        
        Usuario usuario = new Usuario(
                request.getParameter("nomeCompleto"),
                Long.parseLong(request.getParameter("dataNascimento")),
                request.getParameter("apelido"), 
                request.getParameter("email"), 
                request.getParameter("senha"));
        usuario.setIdUsuario(Integer.valueOf(request.getParameter("idUsuario")));
        
        // Chama método para cadastrar usuário
        usuarioDAO.altera(usuario);

        try {
            usuarioDAO.fechaConexao();
            request.getSession().setAttribute("usuarioLogado", usuario);
            response.sendRedirect("feed.jsp");
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
    
    private void buscaUmUsuario(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException{
        
        Usuario usuario = new Usuario();
        usuario = (new UsuarioDAO()).busca(Integer.valueOf(request.getParameter("id")));
        
        request.setAttribute("usarioEncontrado", usuario);
        // Troca de tela pelo Dispatcher
        getServletConfig().getServletContext().getRequestDispatcher("/editaUsuarioJSP.jsp").forward(request, response);
        
    }
    
    private void alteraSenha(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        
        // Recupera o usuario da session http
        Usuario usuario = usuarioDAO.buscaPorEmail(request.getParameter("email"));
        usuario.setSenha(request.getParameter("senha"));
        
        // Chama método para cadastrar usuário
        usuarioDAO.alteraSenha(usuario);

        try {
            usuarioDAO.fechaConexao();
            response.sendRedirect("index.jsp");
        } catch (SQLException ex) {
            Logger.getLogger(UsuarioServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void fazLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Usuario usuario = (new UsuarioDAO()).buscaLogin(
                request.getParameter("email"), 
                request.getParameter("senha")
        );
        
        if (usuario !=  null){
            HttpSession httpSession = request.getSession();
            // Salva na session
            httpSession.setAttribute("usuarioLogado", usuario);
            // Redireciona
            getServletConfig().getServletContext().getRequestDispatcher("/feed.jsp").forward(request, response);
        } else{
            response.sendRedirect("index.jsp");
        }
    }
    
    private void fazLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        request.getSession().invalidate();
        response.sendRedirect("index.jsp");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
