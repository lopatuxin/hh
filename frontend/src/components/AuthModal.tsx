import { useCallback, useEffect } from 'react';
import { X, Save } from 'lucide-react';
import { useSaveAuth, useCancelAuth } from '../api/hooks';

interface AuthModalProps {
  onClose: () => void;
}

export default function AuthModal({ onClose }: AuthModalProps) {
  const saveAuth = useSaveAuth();
  const cancelAuth = useCancelAuth();

  const handleSave = () => {
    saveAuth.mutate(undefined, { onSettled: onClose });
  };

  const handleCancel = useCallback(() => {
    cancelAuth.mutate(undefined, { onSettled: onClose });
  }, [cancelAuth, onClose]);

  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        handleCancel();
      }
    };
    document.addEventListener('keydown', handleEsc);
    return () => document.removeEventListener('keydown', handleEsc);
  }, [handleCancel]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-bg-card border border-border-card rounded-xl overflow-hidden flex flex-col"
           style={{ width: 1024, height: 768 }}>
        <div className="flex items-center justify-between px-4 py-2 border-b border-border-card">
          <span className="text-sm font-medium">Авторизация на hh.ru</span>
          <div className="flex items-center gap-2">
            <button
              onClick={handleSave}
              disabled={saveAuth.isPending}
              className="flex items-center gap-1 px-3 py-1 text-sm bg-accent text-white rounded-lg hover:bg-accent-hover disabled:opacity-50"
            >
              <Save size={14} />
              Сохранить сессию
            </button>
            <button onClick={handleCancel} className="p-1 hover:bg-bg-hover rounded">
              <X size={18} />
            </button>
          </div>
        </div>
        <iframe
          src="/vnc/vnc_lite.html?autoconnect=true&resize=scale"
          className="flex-1 w-full border-0"
          title="hh.ru авторизация"
        />
      </div>
    </div>
  );
}
