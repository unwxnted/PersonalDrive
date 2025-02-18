import React, { useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { CloudUpload } from 'lucide-react';
import { getCookie } from '../utils/cookies';
import OAuth from './OAuth';

export default function Register() {
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');

    useEffect(() => {
        const token = getCookie('jwt');
        if (token) {
            window.location.href = '/';
        }

    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();

        const response = await fetch('http://localhost:8080/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ name, password }),
        });
        if (!response.ok) {
            setMessage(response.statusText);
            return;
        }

        console.log(response);
        let date = new Date();
        date.setTime(date.getTime() + (1 * 60 * 60 * 1000));
        let expires = "expires=" + date.toUTCString();
        document.cookie = `jwt=${await response.text()}; ${expires}; path=/`;
        window.location.href = '/';
    };


    return (
        <div className="d-flex align-items-center justify-content-center vh-100 vw-100">
            <div className="card shadow-lg" style={{ maxWidth: '400px', width: '100%' }}>
                <div className="card-body p-5">
                    <div className="text-center mb-4">
                        <CloudUpload size={48} className="text-primary" />
                        <h2 className="mt-2">PersonalDrive</h2>
                        <p className="text-muted">Create an account</p>
                    </div>
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label htmlFor="name" className="form-label">Username</label>
                            <input
                                type="text"
                                className="form-control"
                                id="name"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required
                            />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="password" className="form-label">Password</label>
                            <input
                                type="password"
                                className="form-control"
                                id="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>
                        <button type="submit" className="btn btn-primary w-100 mt-3">Register</button>
                    </form>

                    <OAuth/>

                    <p className="text-center mt-3 mb-0">
                        do you already have an account? <a href="/login" className="text-primary">Login</a>
                    </p>
                    <div>
                        {message && <p>{message}</p>}
                    </div>
                </div>
            </div>
        </div>
    );
}