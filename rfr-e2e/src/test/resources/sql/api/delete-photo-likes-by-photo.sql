DELETE pl
FROM photo_like pl
         INNER JOIN photo p ON pl.photo_id = p.id
         INNER JOIN `user` u ON p.user_id = u.id
WHERE u.username = :username
