const pool = require('../db/db');
const clients = []

const websocket = (ws, req) => {
    const user_uuid = req.query.user_uuid;
    const chat_uuid = req.params.chat_id;

    const cleanup = () => {
        const index = clients.findIndex(client => client.user_id === user_uuid);
        clients.splice(index, 1);
    };

    clients.push({ ws, user_id: user_uuid, chat_id: chat_uuid });

    ws.on('message', async (message) => {
        const messageObj = JSON.parse(message);
        const receiver = clients.find(client => client.chat_id === messageObj.chat_uuid && client.user_id !== messageObj.user_uuid);
        if (receiver) {
            const send_at = BigInt(new Date().getTime());
            const status = 'received';
            messageObj.send_at = Number(send_at);
            messageObj.status = status;
            let Strmessage = JSON.stringify(messageObj);

            const { user_uuid, chat_uuid, message } = messageObj;
            try {
                await pool.query(
                    "INSERT INTO messages(user_uuid, chat_uuid, message, send_at, status) VALUES($1, $2, $3, $4, $5)",
                    [user_uuid, chat_uuid, message, send_at, status]
                );
                receiver.ws.send(Strmessage);
            } catch (error) {
                console.log(error);
            }
        } else {
            const { user_uuid, chat_uuid, message } = messageObj;
            const send_at = BigInt(new Date().getTime());
            const status = 'sent';
            try {
                await pool.query(
                    "INSERT INTO messages(user_uuid, chat_uuid, message, send_at, status) VALUES($1, $2, $3, $4, $5)",
                    [user_uuid, chat_uuid, message, send_at, status]
                );
            } catch (error) {
                console.log(error);
            }
        }
    });

    ws.on('close', () => {
        cleanup();
    });
};

module.exports = websocket;