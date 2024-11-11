import React, { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { CloudUpload } from 'lucide-react';
import { getCookie } from '../utils/cookies';
import { useEffect } from 'react';

export default function Login() {
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');

    useEffect(() => {
        const token = getCookie('jwt');
        if (token) {
            window.location.href = '/';
        }
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();


        e.preventDefault();

        const response = await fetch('http://localhost:8080/api/users/login', {
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
        let date = new Date();
        date.setTime(date.getTime() + (1 * 60 * 60 * 1000));
        let expires = "expires=" + date.toUTCString();
        document.cookie = `jwt=${await response.text()}; ${expires}; path=/`;
        window.location.href = '/';
    }

    return (
        <div className="d-flex align-items-center justify-content-center vh-100 vw-100">
            <div className="card shadow-lg" style={{ maxWidth: '400px', width: '100%' }}>
                <div className="card-body p-5">
                    <div className="text-center mb-4">
                        <CloudUpload size={48} className="text-primary" />
                        <h2 className="mt-2">PersonalDrive</h2>
                        <p className="text-muted">Login to your account</p>
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
                        <button type="submit" className="btn btn-primary w-100 mt-3">Login</button>
                    </form>
                    <p className="text-center mt-3 mb-0">
                        do not have an account? <a href="/register" className="text-primary">Register</a>
                    </p>
                </div>
            </div>
        </div>
    );

}
