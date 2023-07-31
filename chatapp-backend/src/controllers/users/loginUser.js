const pool = require('../../db/db');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const { CustomError } = require('../../middlewares/error/errorHandler');

const loginUser = async (req, res, next) => {
    try {
        const { username, password } = req.body;


        const user = await pool.query(
            "SELECT * FROM users WHERE username = $1",
            [username]
        );
        if (user.rows.length === 0) {
            return next(
                new CustomError('Username is incorrect', 401)
            )
        } else {
            const isPasswordCorrect = await bcrypt.compare(password, user.rows[0].password);
            if (!isPasswordCorrect) {
                return next(
                    new CustomError('Password is incorrect', 401)
                )
            }
        }

        const token = jwt.sign(
            {
                id: user.rows[0].uuid,
                username: user.rows[0].username
            },
            process.env.JWT_SECRET,
        )

        res.status(200).json({
            status: 200,
            message: user.rows[0].uuid,
            data: {
                token
            }
        })
    } catch (error) {
        next(error);
    }
}

module.exports = {
    loginUser
}