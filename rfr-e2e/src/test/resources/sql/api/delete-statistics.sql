DELETE s
FROM statistic s
         INNER JOIN `user` u ON s.user_id = u.id
WHERE u.username = :username
