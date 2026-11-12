DELETE l
FROM `like` l
         INNER JOIN `user` u ON l.user_id = u.id
WHERE u.username = :username
