const pool = require('../../db/db');

const acceptOrDecline = async (req, res, next) => {
    try {
        const { user_uuid, sender_uuid } = req.body;
        const { accept } = req.query;
        const since = BigInt(new Date().getTime());
        if (accept) {
            //delete request after accepting
            const request = await pool.query(
                "DELETE FROM requests WHERE user_uuid = $1 AND sender_uuid = $2",
                [user_uuid, sender_uuid]
            );
            //insert into friends table
            const friend = await pool.query(
                "INSERT INTO friends (user_uuid, friend_uuid, since) VALUES ($1, $2, $3) RETURNING *",
                [user_uuid, sender_uuid, since]
            );

            const friend2 = await pool.query(
                "INSERT INTO friends (user_uuid, friend_uuid, since) VALUES ($1, $2, $3) RETURNING *",
                [sender_uuid, user_uuid, since]
            );
            // if any error
            if (friend.rowCount === 0 || friend2.rowCount === 0) {
                return res.status(400).json({
                    status: 400,
                    message: 'Request failed',
                    data: null
                })
            }

            res.status(201).json({
                status: 201,
                message: 'request accepted',
                data: null
            })
        } else {
            const request = await pool.query(
                "DELETE FROM requests WHERE user_uuid = $1 AND sender_uuid = $2",
                [user_uuid, sender_uuid]
            );
            res.status(201).json({
                status: 201,
                message: 'request deleted',
                data: null
            })
        }
    }
    catch (error) {
        next(error);
    }
}

module.exports = {
    acceptOrDecline
}