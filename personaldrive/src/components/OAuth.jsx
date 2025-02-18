import React, { useEffect } from 'react';

export default function OAuth() {


    useEffect(() => {
            const urlParams = new URLSearchParams(window.location.search);
            const code = urlParams.get('code');
    
            if (code) {
                handleGoogleCallback(code);
            }
        }, []);


    const handleOauth = async (e) => {
        e.preventDefault();

        const response = await fetch('http://localhost:8080/api/users/google/url');
        const data = await response.json();

        window.location.href = data.url;
    };

    const handleGoogleCallback = async (code) => {
        let date = new Date();
        date.setTime(date.getTime() + (1 * 60 * 60 * 1000));
        let expires = "expires=" + date.toUTCString();
        document.cookie = `jwt=${code}; ${expires}; path=/`;

        window.location.href = '/';

    };


    return (
        <div className="card mt-3 text-center p-2" style={{ cursor: 'pointer', display: 'flex' }} onClick={handleOauth}>
            <span>
                <img
                    src="https://cdn1.iconfinder.com/data/icons/google-s-logo/150/Google_Icons-09-512.png"
                    alt="google"
                    style={{ maxWidth: '30px', marginLeft: '-10px' }}
                />
                Login With Google
            </span>
        </div>
    )

}