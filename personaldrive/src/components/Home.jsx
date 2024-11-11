import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Download, Trash2, Upload, Search } from 'lucide-react';
import { getCookie } from '../utils/cookies';

export default function Home() {
    const [files, setFiles] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [fileToUpload, setFileToUpload] = useState(null);  // Estado para almacenar el archivo seleccionado

    useEffect(() => {
        if(!getCookie('jwt')){
            window.location.href = '/login';
        }
        fetchFiles();
    }, []);

    const fetchFiles = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/files/all', {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
                method: 'GET',
            });
            if (response.ok) {
                const data = await response.json();
                setFiles(data);
            } else {
                console.error('Failed to fetch files');
            }
        } catch (error) {
            console.error('Error fetching files:', error);
        }
    };

    const handleDownload = async (file) => {
        try{
            const response = await fetch(`http://localhost:8080/api/files/download?id=${file.id}`,
                {
                    headers: {
                        'Authorization': `Bearer ${getCookie('jwt')}`,
                    },
                }
            );
            if(response.ok){
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = file.name;
                link.click();
            }
        }catch(error){
            console.error('Error downloading file:', error);
        }
    };

    const handleDelete = async (fileId) => {
        
        try{
            const response = await fetch(`http://localhost:8080/api/files?id=${fileId}`,
                {
                    headers: {
                        'Authorization': `Bearer ${getCookie('jwt')}`,
                    },
                    method: 'DELETE',
                }
            );
            if(response.ok){
                console.log('File deleted successfully');
                fetchFiles();
            }
        }catch(error){
            console.error('Error deleting file:', error);
        }

        await fetchFiles();
    };

    const handleUpload = async () => {
        if (!fileToUpload) {
            console.log('No file selected for upload');
            return;
        }

        const formData = new FormData();
        formData.append('file', fileToUpload);

        try {
            const response = await fetch('http://localhost:8080/api/files/upload', {
                method: 'POST',
                body: formData,
                headers: {
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
            });
            if (response.ok) {
                console.log('File uploaded successfully');
                fetchFiles();
            } else {
                console.error('Error uploading file');
            }
        } catch (error) {
            console.error('Error uploading file:', error);
        }
    };

    const filteredFiles = files.filter(file =>
        file.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="container-fluid bg-light vh-100 vw-100 p-5">
            <div className="container">
                <h1 className="mb-4">PersonalDrive</h1>
                <div className="row mb-4">
                    <div className="col-md-6 mb-3 mb-md-0">
                        <input
                            type="file"
                            onChange={(e) => setFileToUpload(e.target.files[0])}
                            className="form-control"
                        />
                        <button className="btn btn-primary mt-2" onClick={handleUpload}>
                            <Upload size={18} className="me-2" />
                            Subir archivo
                        </button>
                    </div>
                    <div className="col-md-6">
                        <input
                            type="text"
                            className="form-control me-2"
                            placeholder="Buscar archivos..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                </div>
                <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    {filteredFiles.map((file) => (
                        <div key={file.id} className="col">
                            <div className="card h-100">
                                <div className="card-body">
                                    <h5 className="card-title">{file.name}</h5>
                                </div>
                                <div className="card-footer bg-transparent border-top-0">
                                    <button
                                        className="btn btn-outline-primary me-2"
                                        onClick={() => handleDownload(file)}
                                    >
                                        <Download size={18} className="me-1" />
                                        Descargar
                                    </button>
                                    <button
                                        className="btn btn-outline-danger"
                                        onClick={() => handleDelete(file.id)}
                                    >
                                        <Trash2 size={18} className="me-1" />
                                        Eliminar
                                    </button>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
