const pool = require('../../db/db');

const getMessages = async (req, res, next) => {
    const { chat_uuid } = req.params;
    const { user_uuid } = req.query;
    try {
        const client = await pool.connect();

        try {
            const receivedMessagesQuery = "SELECT * FROM messages WHERE chat_uuid = $1 AND status = 'received' ORDER BY send_at DESC";
            const receivedMessagesResult = await client.query(receivedMessagesQuery, [chat_uuid]);
            const receivedMessages = receivedMessagesResult.rows;

            const lastMessagesQuery = `
                SELECT  *
                FROM messages
                WHERE chat_uuid = $1 AND status = 'sent' AND user_uuid != $2
                ORDER BY send_at DESC
            `;
            const lastMessagesResult = await client.query(lastMessagesQuery, [chat_uuid, user_uuid]);
            const lastMessages = lastMessagesResult.rows;

            const markAsReceivedQuery = `
                UPDATE messages
                SET status = 'received'
                WHERE user_uuid != $1 AND chat_uuid = $2 AND status = 'sent'
            `;
            await client.query(markAsReceivedQuery, [user_uuid, chat_uuid]);

            res.status(200).json({
                status: 200,
                message: "Messages retrieved successfully",
                data: {
                    receivedMessages,
                    lastMessages,
                },
            });
        } finally {
            client.release();
        }
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getMessages
}