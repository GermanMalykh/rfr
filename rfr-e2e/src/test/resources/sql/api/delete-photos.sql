DELETE p
FROM photo p
         INNER JOIN `user` u ON p.user_id = u.id
WHERE u.username = :username
