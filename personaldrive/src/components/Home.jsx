import React, { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import { Download, Trash2, Upload, Search, Folder } from 'lucide-react';
import { getCookie } from '../utils/cookies';

export default function Home() {
    const [files, setFiles] = useState([]);
    const [folders, setFolders] = useState([]);
    const [newFolderName, setNewFolderName] = useState('');
    const [currentFolderId, setCurrentFolderId] = useState(0);
    const [lastFolderId, setLastFolderId] = useState();

    const [searchTerm, setSearchTerm] = useState('');
    const [fileToUpload, setFileToUpload] = useState(null);

    useEffect(() => {
        if (!getCookie('jwt')) {
            window.location.href = '/login';
        }
        fetchContents();
    }, [currentFolderId]);

    const fetchContents = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/folders/${currentFolderId}/contents`, {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
                method: 'GET',
            });

            if (response.ok) {
                const data = await response.json();
                setFiles(data.files || []);
                setFolders(data.folders || []);
            } else {
                console.error('Failed to fetch folder contents');
            }
        } catch (error) {
            console.error('Error fetching folder contents:', error);
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
                fetchContents();
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
            const response = await fetch(`http://localhost:8080/api/files/upload?folderId=${currentFolderId}`, {
                method: 'POST',
                body: formData,
                headers: {
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
            });
            if (response.ok) {
                console.log('File uploaded successfully');
                fetchContents();
            } else {
                console.error('Error uploading file');
            }
            setFileToUpload(null);
        } catch (error) {
            console.error('Error uploading file:', error);
        }
    };

    const handleNewFolder = async () => {
        try {
            const response = await fetch(`http://localhost:8080/api/folders/create?name=${newFolderName}&parentId=${currentFolderId}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                }
            });

            if (response.ok) {
                console.log('Folder created successfully');
                fetchContents();
                setNewFolderName('');
            } else {
                console.error('Error creating folder');
            }
        } catch (error) {
            console.error('Error creating folder:', error);
        }
    };

    const handleFolderDelete = async (folderId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/folders/${folderId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${getCookie('jwt')}`,
                },
            });
            if (response.ok) {
                console.log('Folder deleted successfully');
                fetchContents();
            }
        } catch (error) {
            console.error('Error deleting folder:', error);
        }
    };

    const handleCurrentFolderUpdate = (folderId) => {
        setLastFolderId(currentFolderId);
        setCurrentFolderId(folderId); 
        fetchContents();
        console.log(currentFolderId);
        console.log(folderId);
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

                {currentFolderId > 0 && <button className='btn btn-outline-secondary m-1' onClick={() => {
                    handleCurrentFolderUpdate(lastFolderId);
                }}>
                    Go back
                </button>}

                
                
                {currentFolderId > 0 && lastFolderId > 0 && <button className='btn btn-outline-primary m-1 ' onClick={() => {
                    handleCurrentFolderUpdate(0);
                }}>
                    Home
                </button>}


                
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
                            className="form-control me-2 h-auto w-auto"
                        />
                        <button className="btn btn-primary" onClick={handleUpload}>
                            <Upload size={18} className="me-2" />
                            Upload
                        </button>
                    </div>
                    <div className="col-md-6 mb-3 mb-md-0 d-flex align-items-center justify-content-end">
                        <input
                            type="text"
                            placeholder='New folder name'
                            onChange={(e) => setNewFolderName(e.target.value)}
                            className="form-control me-2 h-auto w-auto"
                        />
                        <button className="btn btn-primary" onClick={handleNewFolder}>
                            <Upload size={18} className="me-2" />
                            Folder
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
                                    </h5>
                                </div>
                                <div className="card-footer bg-transparent border-top-0">
                                    <button
                                        className="btn btn-outline-primary me-2"
                                        onClick={() => handleCurrentFolderUpdate(folder.id)}
                                    >
                                        <Download size={18} className="me-1" />
                                        Enter
                                    </button>
                                    <button
                                        className="btn btn-outline-danger me-2"
                                        onClick={() => handleFolderDelete(folder.id)}
                                    >
                                        <Trash2 size={18} className="me-1" />
                                        Delete
                                    </button>
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
                                    <p>Size: {Math.trunc(file.size/1024)} KB</p>
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
