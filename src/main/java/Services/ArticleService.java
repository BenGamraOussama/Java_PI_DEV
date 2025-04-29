package Services;

import Entities.Article;
import Utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ArticleService implements IArticle<Article> {
    private final Connection connection;

    public ArticleService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Article a) throws SQLException {
        String query = "INSERT INTO article (category_id, user_id, title, content, media_path, published_at) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
        ps.setInt(1, a.getCategoryId());
        ps.setInt(2, a.getUserId());
        ps.setString(3, a.getTitle());
        ps.setString(4, a.getContent());
        ps.setString(5, a.getMediaPath());
        ps.setDate(6, Date.valueOf(a.getPublishedAt()));
        ps.executeUpdate();

        ResultSet generatedKeys = ps.getGeneratedKeys();
        if (generatedKeys.next()) {
            a.setId(generatedKeys.getInt(1)); // Update the article object with the real ID
        }
    }

    @Override
    public void modifier(Article a) throws SQLException {
        String query = "UPDATE article SET category_id=?, title=?, content=?, media_path=?, published_at=? WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, a.getCategoryId());
        ps.setString(2, a.getTitle());
        ps.setString(3, a.getContent());
        ps.setString(4, a.getMediaPath());
        ps.setDate(5, Date.valueOf(a.getPublishedAt()));
        ps.setInt(6, a.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM article WHERE id=?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    @Override
    public List<Article> afficher() throws SQLException {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM article";
        Statement stmt = MyDatabase.getInstance().getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(query);

        while (rs.next()) {
            Article article = new Article(
                    rs.getInt("id"),
                    rs.getInt("category_id"),
                    rs.getInt("user_id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("media_path"),
                    rs.getDate("published_at").toLocalDate()
            );
            articles.add(article);
        }

        return articles;
    }

}
