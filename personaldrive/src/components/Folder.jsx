import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Download, Trash2, Upload, Search, Folder } from 'lucide-react';
import { getCookie } from '../utils/cookies';
import { redirect } from 'react-router-dom';

export default function Home() {
    const [files, setFiles] = useState([]);
    const [folders, setFolders] = useState([]);
    const [newFolderName, setNewFolderName] = useState('');

    const [searchTerm, setSearchTerm] = useState('');
    const [fileToUpload, setFileToUpload] = useState(null);

    useEffect(() => {
        if (!getCookie('jwt')) {
            window.location.href = '/login';
        }
        fetchFiles();
        fetchFolders();
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
                console.log(data);
                setFiles(data);
            } else {
                console.error('Failed to fetch files');
            }
        } catch (error) {
            console.error('Error fetching files:', error);
        }
    };

    const fetchFolders = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/folders/home', {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
                method: 'GET',
            });
            if (response.ok) {
                const data = await response.json();
                setFolders(data);
            } else {
                console.error('Failed to fetch folders');
            }
        } catch (error) {
            console.error('Error fetching folders:', error);
        }
    };

    const handleDownload = async (file) => {
        try {
            const response = await fetch(`http://localhost:8080/api/files/download?id=${file.id}`, {
                headers: {
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
            });
            if (response.ok) {
                const blob = await response.blob();
                const url = window.URL.createObjectURL(blob);
                const link = document.createElement('a');
                link.href = url;
                link.download = file.name;
                link.click();
            }
        } catch (error) {
            console.error('Error downloading file:', error);
        }
    };

    const handleDelete = async (fileId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/files?id=${fileId}`, {
                headers: {
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
                method: 'DELETE',
            });
            if (response.ok) {
                console.log('File deleted successfully');
                fetchFiles();
            }
        } catch (error) {
            console.error('Error deleting file:', error);
        }
    };

    const handleUpload = async () => {
        if (!fileToUpload) {
            console.log('No file selected for upload');
            return;
        }

        const formData = new FormData();
        formData.append('file', fileToUpload);

        try {
            const response = await fetch(`http://localhost:8080/api/files/upload?folderId=${0}`, {
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

    const handleNewFolder = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/folders?name=${newFolderName}&parentId=${0}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                }
            });

            if (response.ok) {
                console.log('Folder created successfully');
                fetchFiles();
                fetchFolders();
                setNewFolderName('');
            } else {
                console.error('Error creating folder');
            }
        } catch (error) {
            console.error('Error creating folder:', error);
        }
    };

    const filteredFiles = files.filter(file =>
        file.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const filteredFolders = folders.filter(folder =>
        folder.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="container-fluid bg-light vh-100 vw-100 p-5">
            <div className="container">
                <h1 className="mb-4">PersonalDrive</h1>
                <div className="row mb-4">
                    <div className="col-md-15 mb-3 mb-md-3">
                        <input
                            type="text"
                            className="form-control me-2"
                            placeholder="Search files and folders..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                    <div className="col-md-6 mb-3 mb-md-0 d-flex align-items-center">
                        <input
                            type="file"
                            onChange={(e) => setFileToUpload(e.target.files[0])}
                            className="form-control me-2"
                        />
                        <button className="btn btn-primary" onClick={handleUpload}>
                            <Upload size={18} className="me-2" />
                            Upload file
                        </button>
                    </div>
                    <div className="col-md-6 mb-3 mb-md-0 d-flex align-items-center">
                        <input
                            type="text"
                            onChange={(e) => setNewFolderName(e.target.value)}
                            className="form-control me-2"
                        />
                        <button className="btn btn-primary" onClick={handleNewFolder}>
                            <Upload size={18} className="me-2" />
                            New folder
                        </button>
                    </div>

                </div>
                <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
                    {filteredFolders.map((folder) => (
                        <div key={folder.id} className="col">
                            <div className="card h-100">
                                <div className="card-body">
                                    <h5 className="card-title">
                                        <Folder size={18} className="me-2" />
                                        {folder.name}
                                        <button className='btn btn-outline-primary me-2'
                                        onClick={() => redirect(`/folder/${folder.id}`)}>
                                            Enter
                                        </button>
                                    </h5>
                                </div>
                            </div>
                        </div>
                    ))}
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
                                        Download
                                    </button>
                                    <button
                                        className="btn btn-outline-danger"
                                        onClick={() => handleDelete(file.id)}
                                    >
                                        <Trash2 size={18} className="me-1" />
                                        Delete
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
