const pool = require('../../db/db');
const { v4: uuidv4 } = require('uuid');

const createChat = async (req, res, next) => {
    try {
        const { user_uuid, friend_uuid } = req.body;
        const chat_uuid = uuidv4();
        const create_at = BigInt(new Date().getTime());

        const me = await pool.query(
            "SELECT * FROM users WHERE uuid = $1",
            [user_uuid]
        );

        const friend = await pool.query(
            "SELECT * FROM users WHERE uuid = $1",
            [friend_uuid]
        );

        const user = await pool.query(
            "INSERT INTO chats (chat_uuid, user_uuid, chatname, created_at) VALUES ($1, $2, $3, $4) RETURNING *",
            [chat_uuid, user_uuid, friend.rows[0].username, create_at]
        );

        const user2 = await pool.query(
            "INSERT INTO chats (chat_uuid, user_uuid, chatname, created_at) VALUES ($1, $2, $3, $4) RETURNING *",
            [chat_uuid, friend_uuid, me.rows[0].username, create_at]
        );
        res.status(201).json({
            status: 201,
            message: 'chat created',
            data: user.rows[0]
        })
    } catch (error) {
        next(error);
    }
}

module.exports = {
    createChat
}