import React from 'react';
import { Sun, Moon, Bell, Search, RefreshCw } from 'lucide-react';
import { Button, Input, Avatar, Badge } from '@heroui/react';

interface NavbarProps {
  title: string;
  subtitle: string;
  isDark: boolean;
  toggleTheme: () => void;
  onRefresh?: () => void;
  isLoading?: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({
  title,
  subtitle,
  isDark,
  toggleTheme,
  onRefresh,
  isLoading = false,
}) => {
  return (
    <header className="h-16 border-b border-neutral-200 dark:border-neutral-800 bg-white/80 dark:bg-neutral-900/80 backdrop-blur-md px-6 flex items-center justify-between sticky top-0 z-30">
      <div>
        <h2 className="text-lg font-bold text-neutral-900 dark:text-white leading-tight">{title}</h2>
        <p className="text-xs text-neutral-500 dark:text-neutral-400">{subtitle}</p>
      </div>

      <div className="flex items-center gap-3">
        {onRefresh && (
          <Button
            isIconOnly
            size="sm"
            variant="light"
            onClick={onRefresh}
            isLoading={isLoading}
            className="text-neutral-500 hover:text-neutral-900 dark:hover:text-white"
          >
            <RefreshCw className="w-4 h-4" />
          </Button>
        )}

        <Button
          isIconOnly
          size="sm"
          variant="flat"
          onClick={toggleTheme}
          className="bg-neutral-100 dark:bg-neutral-800 text-neutral-700 dark:text-neutral-300"
        >
          {isDark ? <Sun className="w-4 h-4 text-amber-400" /> : <Moon className="w-4 h-4 text-neutral-600" />}
        </Button>

        <Badge content="3" color="danger" size="sm" shape="circle">
          <Button
            isIconOnly
            size="sm"
            variant="light"
            className="text-neutral-600 dark:text-neutral-400"
          >
            <Bell className="w-4 h-4" />
          </Button>
        </Badge>

        <div className="h-6 w-[1px] bg-neutral-200 dark:bg-neutral-800 mx-1" />

        <div className="flex items-center gap-2.5">
          <Avatar
            name="Admin"
            size="sm"
            className="bg-blue-600 text-white font-bold"
          />
          <div className="hidden md:block text-left">
            <p className="text-xs font-semibold text-neutral-900 dark:text-neutral-100">Operations Manager</p>
            <p className="text-[10px] text-neutral-400">admin@fooderp.com</p>
          </div>
        </div>
      </div>
    </header>
  );
};
