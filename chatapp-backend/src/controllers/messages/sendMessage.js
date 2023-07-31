const pool = require('../../db/db');

const sendMessage = async (req, res, next) => {
    try {
        const { user_uuid, chat_uuid, message } = req.body;
        const send_at = BigInt(new Date().getTime());
        const new_message = await pool.query(
            "INSERT INTO messages (user_uuid, chat_uuid, message, send_at) VALUES ($1, $2, $3, $4) RETURNING *",
            [user_uuid, chat_uuid, message, send_at]
        );
        await pool.query(
            "UPDATE messages SET status = 'sent' WHERE user_uuid = $1 AND chat_uuid = $2",
            [user_uuid, chat_uuid]
        );
        res.status(201).json({
            status: 'success',
            data: new_message.rows
        })
    }
    catch (error) {
        next(error);
    }
}

module.exports = {
    sendMessage
}