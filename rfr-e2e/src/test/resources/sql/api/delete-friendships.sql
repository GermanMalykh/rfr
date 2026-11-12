DELETE f
FROM friendship f
         INNER JOIN `user` u ON u.id IN (f.requester_id, f.addressee_id)
WHERE u.username = :username
