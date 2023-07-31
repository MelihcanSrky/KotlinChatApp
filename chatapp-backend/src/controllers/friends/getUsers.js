const pool = require('../../db/db');

const getUsers = async (req, res, next) => {
    try {
        const { user_uuid } = req.params;
        const { search } = req.query;
        // search parameter take username or firstname or lastname
        // take users with contains search parameter

        if (!search) {
            return res.status(400).json({
                status: 400,
                message: 'search parameter is required',
                data: null
            })
        }

        const users = await pool.query(
            "SELECT * FROM users WHERE uuid != $1 AND (username LIKE $2 OR firstname LIKE $2 OR lastname LIKE $2)",
            [user_uuid, `%${search}%`]
        );

        res.status(200).json({
            status: 200,
            message: 'success',
            data: users.rows
        })
    } catch (error) {
        next(error);
    }
}

module.exports = {
    getUsers
}