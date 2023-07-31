const pool = require('../../db/db');

const sendRequest = async (req, res, next) => {
    try {
        const { user_uuid, sender_uuid } = req.body;
        const send_at = BigInt(new Date().getTime());
        const request = await pool.query(
            "INSERT INTO requests (user_uuid, sender_uuid, send_at) VALUES ($1, $2, $3) RETURNING *",
            [user_uuid, sender_uuid, send_at]
        );
        if (request.rowCount === 0) {
            return res.status(400).json({
                status: 400,
                message: 'Request failed',
                data: null
            })
        }
        res.status(201).json({
            status: 201,
            message: 'Request sent successfully',
            data: null
        })
    } catch (error) {
        next(error);
    }
}

module.exports = {
    sendRequest
}